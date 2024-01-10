package com.example.app.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ BadCredentialsException.class })
    public ResponseEntity<?> handleBadCredentialException(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "Bad Credentials!",
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({ JsonDataFormatException.class })
    public ResponseEntity<?> handleJsonDataFormatException(
            Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, "Wrong JSON Data Format!",
                new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler ({SignatureException.class})
    public ResponseEntity<?> handleSignatureException(
            Exception ex, WebRequest request){
        return handleExceptionInternal(ex, "Invalid JWT token!",
                new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler ({ExpiredJwtException.class})
    public ResponseEntity<?> handleExpiredJwtException(
            Exception ex, WebRequest request){
        return handleExceptionInternal(ex, "Expired JWT token, generate new one!",
                new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler ({UserDataException.class})
    public ResponseEntity<?> handleUserDataException(
            Exception ex, WebRequest request){
        return handleExceptionInternal(ex, "No username or password provided!",
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
