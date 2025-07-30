package com.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

	Optional<Client> findByEmail(String email);

}
