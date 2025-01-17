package com.example.userservice.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String role;
}
