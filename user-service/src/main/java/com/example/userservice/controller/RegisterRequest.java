package com.example.userservice.controller;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RegisterRequest implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
