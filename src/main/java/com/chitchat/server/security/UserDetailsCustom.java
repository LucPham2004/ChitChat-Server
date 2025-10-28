package com.chitchat.server.security;

import com.chitchat.server.entity.User;
import com.chitchat.server.exception.AppException;
import com.chitchat.server.exception.ErrorCode;
import com.chitchat.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component("userDetailsService")
@RequiredArgsConstructor
@Slf4j
public class UserDetailsCustom implements UserDetailsService {

     // private final UserService userService;
     private final UserRepository userRepository;

     @Override
     public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
          Optional<User> userOptional = userRepository.findByEmailAndIsActiveTrue(login);

          if (userOptional.isEmpty()) {
               log.info("email empty");
               userOptional = userRepository.findByPhoneAndIsActiveTrue(login);
          }

          if (userOptional.isEmpty()) {
               log.info("phone empty");
               throw new UsernameNotFoundException("User not found or not active");
          }

          // Lấy user từ Optional nếu có
          User user = userOptional.get();
          
          return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
     }

}
