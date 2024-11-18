package com.alibou.book.exception;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.alibou.book.auth.RegisterResponse;
import com.alibou.book.project.ProjectResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import io.jsonwebtoken.security.SignatureException;

import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {


    // Not working...
    // @ExceptionHandler({SignatureException.class, MalformedJwtException.class, ExpiredJwtException.class, UnsupportedJwtException.class})
    // public ResponseEntity<Object> handleJwtExceptions(Exception ex) {
    //     Map<String, Object> body = new HashMap<>();
    //     body.put("success", false);
    //     body.put("message", "Invalid or expired token");
    
    //     return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    // }
    

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        return new ResponseEntity<>(
            new Response(false, "Validierungsfehler: " + errorMessage),
            HttpStatus.BAD_REQUEST
        );
    }

}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Response {
    private boolean success;
    private String message;
}
