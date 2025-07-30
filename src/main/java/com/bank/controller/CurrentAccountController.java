package com.bank.controller;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.CurrentAccountTransactionRequest;
import com.bank.exception.InvalidTransactionException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;

import com.bank.service.CurrentAccountService;

@RestController
@RequestMapping("/current-account")
public class CurrentAccountController {

	private final CurrentAccountService currentAccountService;

	public CurrentAccountController(CurrentAccountService currentAccountService) {
		this.currentAccountService = currentAccountService;

	}

	@GetMapping("/balance")
	public ResponseEntity<BigDecimal> getBalance(@RequestParam String email) {
		return ResponseEntity.ok(currentAccountService.getBalance(email));
	}

	@PostMapping("/deposit/{accountId}")
	public ResponseEntity<?> deposit(@PathVariable Long accountId,
			@RequestBody CurrentAccountTransactionRequest request, Authentication authentication) {
		String userEmail = authentication.getName();

		try {
			Account updateAccount = currentAccountService.deposit(accountId, userEmail, request.getAmount());
			return ResponseEntity.ok(updateAccount);
		} catch (InvalidTransactionException | ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		}
	}

	@PostMapping("/withdraw/{accountId}")
	public ResponseEntity<?> withdraw(@PathVariable Long accountId,
			@RequestBody CurrentAccountTransactionRequest request, Authentication authentication) {

		String userEmail = authentication.getName();

		try {
			Account updateAccount = currentAccountService.withdraw(accountId, userEmail, request.getAmount());
			return ResponseEntity.ok(updateAccount);
		} catch (InvalidTransactionException | ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		}
	}
}
