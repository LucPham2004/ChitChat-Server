package com.chitchat.server.controller;

import com.chitchat.server.dto.request.LoginRequest;
import com.chitchat.server.dto.request.UserCreationRequest;
import com.chitchat.server.dto.request.UserUpdateOtpRequest;
import com.chitchat.server.dto.response.ApiResponse;
import com.chitchat.server.dto.response.LoginResponse;
import com.chitchat.server.dto.response.UserResponse;
import com.chitchat.server.entity.Role;
import com.chitchat.server.entity.User;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.mapper.UserMapper;
import com.chitchat.server.repository.RoleRepository;
import com.chitchat.server.service.impl.UserServiceImpl;
import com.chitchat.server.utils.EmailUtil;
import com.chitchat.server.utils.OtpUtil;
import com.chitchat.server.utils.SecurityUtils;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    AuthenticationManagerBuilder authenticationManagerBuilder;
    UserServiceImpl userService;
    RoleRepository roleRepository;
    UserMapper userMapper;
    SecurityUtils securityUtils;
    EmailUtil emailUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword());
        log.info("authenticationToken: {}", authenticationToken);

        // Authenticate the user
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("authentication principal: {}", authentication.getPrincipal());

        // Prepare the login response
        LoginResponse loginResponse = new LoginResponse();
        User currentUserDB = userService.handleGetUserByLoginInput(loginRequest.getUsername());

        Set<Role> authorities = currentUserDB.getAuthorities().stream()
				.map(roleRepository::findByAuthority)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet());

        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(
                currentUserDB.getId(),
                currentUserDB.getEmail(),
                currentUserDB.getUsername(),
                currentUserDB.getAvatarUrl(),
                currentUserDB.getFirstName(),
                currentUserDB.getLastName(),
                currentUserDB.getLocation(),
                currentUserDB.getBio(),
                authorities);
        loginResponse.setUser(userLogin);

        // Generate tokens
        String access_token = securityUtils.createAccessToken(authentication.getName(), loginResponse);
        loginResponse.setAccess_token(access_token);

        String refresh_token = securityUtils.createRefreshToken(loginRequest.getUsername(), loginResponse);

        // Update refresh token for the user in the database
        userService.updateUserToken(refresh_token, loginRequest.getUsername());

        // Set refresh token as an HTTP-only cookie
        ResponseCookie resCookie = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(securityUtils.refreshTokenExpiration)
                .build();

        // Create the ApiResponse
        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .code(1000)
                .message("Login successfully")
                .result(loginResponse)
                .build();

        // Build the response with the body and headers
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString()) // Set the cookie in the response header
                .body(apiResponse); // Return the body with the API response
    }

    @GetMapping("/account")
    public ApiResponse<LoginResponse.UserGetAccount> getAccount() {
        String login = SecurityUtils.getCurrentUserLogin().isPresent()
                ? SecurityUtils.getCurrentUserLogin().get()
                : "";

        User currentUserDB = userService.handleGetUserByLoginInput(login);

        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin();
        LoginResponse.UserGetAccount userGetAccount = new LoginResponse.UserGetAccount();

        Set<Role> authorities = currentUserDB.getAuthorities().stream()
				.map(roleRepository::findByAuthority)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet());

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setUsername(currentUserDB.getUsername());
            userLogin.setAuthorities(authorities);

            userGetAccount.setUser(userLogin);
        }

        return ApiResponse.<LoginResponse.UserGetAccount>builder()
                .code(1000)
                .message("Get current user successfully!")
                .result(userGetAccount)
                .build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "blabla") String refresh_token, String message) {
        if (refresh_token.equals("blabla")) {
            throw new AppException(ErrorCode.NO_REFRESH_TOKEN);
        }
        // check valid
        Jwt decodedToken = this.securityUtils.checkValidRefreshToken(refresh_token);
        String emailUsernamePhone = decodedToken.getSubject();

        // check user by token + email
        User currentUser = userService.getUserByRefreshTokenAndEmailOrUsernameOrPhone(refresh_token,
                emailUsernamePhone);

        // issue new token / set refresh token as cookies
        LoginResponse res = new LoginResponse();

        Set<Role> authorities = currentUser.getAuthorities().stream()
				.map(roleRepository::findByAuthority)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet());

        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getUsername(),
                authorities);
        res.setUser(userLogin);

        String access_token = this.securityUtils.createAccessToken(emailUsernamePhone, res);

        res.setAccess_token(access_token);

        // create refresh token
        String new_refresh_token = this.securityUtils.createRefreshToken(emailUsernamePhone, res);

        // update refreshToken for user
        this.userService.updateUserToken(new_refresh_token, emailUsernamePhone);

        // set cookies
        ResponseCookie resCookie = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(securityUtils.refreshTokenExpiration)
                .build();

        ApiResponse<LoginResponse> apiResponse = new ApiResponse<>();

        apiResponse.setCode(1000);
        apiResponse.setMessage(message);
        apiResponse.setResult(res);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        String emailUsernamePhone = SecurityUtils.getCurrentUserLogin().isPresent()
                ? SecurityUtils.getCurrentUserLogin().get()
                : "";

        if (emailUsernamePhone.equals("")) {
            throw new AppException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        // update refresh token = null
        this.userService.updateUserToken(null, emailUsernamePhone);

        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setCode(1000);
        apiResponse.setMessage("Log out successfully!");

        // remove fresh token from cookie`
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(apiResponse);

    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody UserCreationRequest reqUser) {
        User resUser = userService.createUser(reqUser);

		// UserUpdateOtpRequest updateUser = new UserUpdateOtpRequest();

        // String otp = OtpUtil.generateOtp(6);
        // updateUser.setOtp(otp);
        // updateUser.setOtpGeneratedTime(Instant.now());

        // try {
        //     emailUtil.sendOtpEmail(reqUser.getEmail(), otp);

        // } catch (MessagingException e) {
        //     throw new AppException(ErrorCode.ERROR_EMAIL);
        // }

        // userServiceClient.updateUserOtp(updateUser);

        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("register successfully!")
                .result(userMapper.toUserResponse(resUser))
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<Void> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        User user = userService.handleGetUserByLoginInput(email);

        boolean isVerified = userService.verifyOtp(user.getId(), otp);
        if (isVerified) {
			UserUpdateOtpRequest updateUser = new UserUpdateOtpRequest();

            updateUser.setActive(true);
            userService.updateUserOtp(updateUser);

            return ApiResponse.<Void>builder()
                    .code(1000)
                    .message("Account verified successfully!")
                    .build();
        } else {
            return ApiResponse.<Void>builder()
                    .code(1001)
                    .message("Invalid or expired OTP.")
                    .build();
        }
    }

    @PostMapping("/regenerate-otp")
    public ApiResponse<String> regenerateOtp(@RequestParam String email) {
        // UserAuthResponse user = userServiceClient.handleGetUserByUsernameOrEmailOrPhone(email)
		// 	.getResult();

        String otp = OtpUtil.generateOtp(6);
        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.ERROR_EMAIL);
        }

		UserUpdateOtpRequest updateUser = new UserUpdateOtpRequest();

        updateUser.setOtp(otp);
        updateUser.setOtpGeneratedTime(Instant.now());
        userService.updateUserOtp(updateUser);
        return ApiResponse.<String>builder()
                .code(1000)
                .message("New OTP sent to email.")
                .result("OTP regenerated")
                .build();
    }

}
