package com.example.userservice.service;

import com.example.userservice.model.AccountDetails;
import com.example.userservice.model.UserRole;
import com.example.userservice.repository.AccountDetailsRepository;
import com.example.userservice.service.exception.EmailAlreadyUsedException;
import com.example.userservice.service.exception.EmailNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AccountDetailsRepository accountDetailsRepository;

    public AccountDetailsResponse createAccount(final String firstName,
                                                final String lastName,
                                                final String email,
                                                final String password,
                                                final UserRole role) {
        if (accountDetailsRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException("This email is already in use");
        }
        AccountDetails accountDetails = new AccountDetails(firstName, lastName, email, passwordEncoder.encode(password), role);
        accountDetailsRepository.save(accountDetails);
        return new AccountDetailsResponse(
                accountDetails.getAccountId(),
                accountDetails.getFirstName(),
                accountDetails.getLastName(),
                accountDetails.getRole().toString());
    }

    public AccountDetailsResponse login(final String email, final String password) {
        if (!accountDetailsRepository.existsByEmail(email)) {
            throw new EmailNotFoundException("There is no account with this email");
        }
        Authentication authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(email, password));
        AccountDetails accountDetails = (AccountDetails) authentication.getPrincipal();
        return new AccountDetailsResponse(
                accountDetails.getAccountId(),
                accountDetails.getFirstName(),
                accountDetails.getLastName(),
                accountDetails.getRole().toString());
    }
}
