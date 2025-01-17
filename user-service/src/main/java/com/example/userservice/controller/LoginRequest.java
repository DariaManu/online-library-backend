package com.example.userservice.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class LoginRequest implements Serializable {
    private String email;
    private String password;
}
