package com.alibou.book.auth;

import org.springframework.boot.autoconfigure.batch.BatchDataSource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@BatchDataSource
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

    private boolean success;
    private String message;

}
