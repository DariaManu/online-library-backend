package com.example.userservice.repository;

import com.example.userservice.model.AccountDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailsRepository extends CrudRepository<AccountDetails, Long> {
    AccountDetails findByEmail(final String email);
    Boolean existsByEmail(final String email);
}
