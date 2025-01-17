package com.example.userservice.controller;

import com.example.userservice.model.AccountDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorize")
@AllArgsConstructor
public class AuthorizationController {

    @GetMapping(path = "/user")
    public ResponseEntity<?> authorizeUserAccessToServices(Authentication authentication) {
        AccountDetails principal = (AccountDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(principal.getAccountId());
    }

    @GetMapping(path = "/admin")
    public ResponseEntity<?> authorizeAdminAccessToService(Authentication authentication) {
        AccountDetails principal = (AccountDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(principal.getAccountId());
    }

    @GetMapping(path = "/all")
    public ResponseEntity<?> authorizeAllAccessToService(Authentication authentication) {
        AccountDetails principal = (AccountDetails) authentication.getPrincipal();
        return ResponseEntity.ok().body(principal.getAccountId());
    }

    @GetMapping(path = "/test1")
    public ResponseEntity<?> test1() {
        return ResponseEntity.ok().body("Needs USER role");
    }

    @GetMapping(path = "/test2")
    public ResponseEntity<?> test2() {
        return ResponseEntity.ok().body("Needs ADMIN role");
    }

    @GetMapping(path = "/test3")
    public ResponseEntity<?> test3() {
        return ResponseEntity.ok().body("Needs AUTHENTICATION");
    }
}
