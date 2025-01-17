package com.example.userservice.controller;

import com.example.userservice.model.UserRole;
import com.example.userservice.service.AccountDetailsResponse;
import com.example.userservice.service.AuthenticationService;
import com.example.userservice.service.exception.EmailAlreadyUsedException;
import com.example.userservice.service.exception.EmailNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(path = "/login")
    public ResponseEntity<AccountDetailsResponse> login(@RequestBody LoginRequest request) {
        AccountDetailsResponse accountDetailsResponse = null;
        try {
            accountDetailsResponse = authenticationService.login(request.getEmail(), request.getPassword());
        } catch (EmailNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (BadCredentialsException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().body(accountDetailsResponse);
    }

    @PostMapping(path = "/register/user")
    public ResponseEntity<AccountDetailsResponse> registerUser(@RequestBody RegisterRequest request) {
        AccountDetailsResponse accountDetailsResponse = null;
        try {
            accountDetailsResponse = authenticationService.createAccount(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPassword(),
                    UserRole.USER);
        } catch (EmailAlreadyUsedException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().body(accountDetailsResponse);
    }

    @PostMapping(path = "/register/admin")
    public ResponseEntity<AccountDetailsResponse> registerAdministrator(@RequestBody RegisterRequest request) {
        AccountDetailsResponse accountDetailsResponse = null;
        try {
            accountDetailsResponse = authenticationService.createAccount(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPassword(),
                    UserRole.ADMINISTRATOR);
        } catch (EmailAlreadyUsedException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().body(accountDetailsResponse);
    }
}