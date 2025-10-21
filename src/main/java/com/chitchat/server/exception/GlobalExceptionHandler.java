package com.chitchat.server.exception;

import com.chitchat.server.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.Objects;


@Slf4j
@ControllerAdvice
 public class GlobalExceptionHandler {

     @ExceptionHandler(value = Exception.class)
     public ResponseEntity<ApiResponse<String>> handlingException(Exception exception) {
         ApiResponse<String> apiResponse = new ApiResponse<>();

         apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
         apiResponse.setMessage(exception.getMessage() + "\n" + Arrays.toString(exception.getStackTrace()));
         log.info(apiResponse.getMessage());

         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
     }

     @ExceptionHandler(value = AppException.class)
     public ResponseEntity<ApiResponse<String>> handleAppException(AppException exception) {
         ErrorCode errorCode = exception.getErrorCode();

         ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                 .code(errorCode.getCode())
                 .message(errorCode.getMessage())
                 .result(exception.getMessage())
                 .build();
         log.info(apiResponse.getMessage());

         return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
     }

     @ExceptionHandler(value = MethodArgumentNotValidException.class)
     public ResponseEntity<ApiResponse<String>> handlingValidation(MethodArgumentNotValidException exception) {
         String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();

         ErrorCode errorCode = ErrorCode.INVALID_KEY;

         ApiResponse<String> apiResponse = new ApiResponse<>();
         apiResponse.setMessage(exception.getMessage() + "\n" + Arrays.toString(exception.getStackTrace()));

         try {
             errorCode = ErrorCode.valueOf(enumKey);
         } catch(IllegalArgumentException e) {
             log.info(apiResponse.getMessage());
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
         }

         apiResponse.setCode(errorCode.getCode());
         apiResponse.setMessage(errorCode.getMessage());
         log.info(apiResponse.getMessage());

         return ResponseEntity.badRequest().body(apiResponse);
     }
 }
