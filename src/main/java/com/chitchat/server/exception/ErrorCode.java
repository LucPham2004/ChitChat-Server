package com.chitchat.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Đã có sự cố trong lúc xử lý", HttpStatus.INTERNAL_SERVER_ERROR),

    UNAUTHENTICATED(1001, "Người dùng chưa được xác thực", HttpStatus.UNAUTHORIZED),

    ACCOUNT_EXISTED(1002, "Tài khoản đã tồn tại!", HttpStatus.BAD_REQUEST),

    ENTITY_NOT_EXISTED(1003, "Không tìm thấy đối tượng!", HttpStatus.NOT_FOUND),

    NO_REFRESH_TOKEN(1004, "You don't have refresh token in cookies", HttpStatus.BAD_REQUEST),

    INVALID_ACCESS_TOKEN(1005, "Your access token is not valid", HttpStatus.BAD_REQUEST),

    ERROR_EMAIL(1006, "Một số lỗi xảy ra khi gửi email", HttpStatus.INTERNAL_SERVER_ERROR),

    PASSWORD_NOT_MATCH(1007, "Mật khẩu và mật khẩu xác nhận không khớp", HttpStatus.BAD_REQUEST),

    INVALID_TOKEN(1008, "Invalid or expired reset token", HttpStatus.BAD_REQUEST),

    TOKEN_EXPIRED(1009, "Trang đặt lại mật khẩu này đã hết hạn. Vui lòng thử lại sau!", HttpStatus.OK),

    INVALID_PARAMETERS(1010, "Uncategorized error", HttpStatus.BAD_REQUEST),

    ENTITY_NOT_EXISTED_OR_NOT_ACTIVE(1011, "Tài khoản của bạn không tồn tại hoặc chưa hoạt động!", HttpStatus.NOT_FOUND),

    CREDENTIAL_NOT_MATCH(1011, "Email và mật khẩu không khớp! Vui lòng thử lại.!", HttpStatus.OK),

    INVALID_OTP(1013, "OTP không hợp lệ", HttpStatus.OK),

    EXPIRED_OTP(1014, "OTP đã hết hạn", HttpStatus.OK)
    ;

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;
}
