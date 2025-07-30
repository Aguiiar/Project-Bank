package com.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.model.Account;
import com.bank.model.Client;

public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByClient(Client client);

}
