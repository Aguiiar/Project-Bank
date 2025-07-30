package com.bank.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bank.exception.InvalidTransactionException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.Client;
import com.bank.repository.AccountRepository;
import com.bank.repository.ClientRepository;

@Service
public class CurrentAccountService {

	private final AccountRepository accountRepository;
	private final ClientRepository clientRepository;

	public CurrentAccountService(AccountRepository accountRepository, ClientRepository clientRepository) {
		this.accountRepository = accountRepository;
		this.clientRepository = clientRepository;

	}

	public Account findAccountByClientEmail(String email) {

		Client client = clientRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Client with email" + email + " not found"));

		return accountRepository.findByClient(client)
				.orElseThrow(() -> new ResourceNotFoundException("Client account " + email + " not found"));

	}



	public Account deposit(Long accountId, String email, BigDecimal amount) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Deposit amount must be greater than zero");

		}
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));

		if (!account.getClient().getEmail().equals(email)) {
			throw new InvalidTransactionException("You can only deposit into your own account");

		}

		account.setBalance(account.getBalance().add(amount));
		return accountRepository.save(account);

	}

	public boolean accountBelongsToUser(Long accountId, String userEmail) {
		Optional<Account> account = accountRepository.findById(accountId);
		if (account.isPresent()) {
			Client owner = account.get().getClient();
			return owner.getEmail().equals(userEmail);

		}
		return false;

	}

	public Account withdraw(Long accountId, String email, BigDecimal amount) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Invalid withdrawal: value must be greater than zero"
					+ "");

		}

		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));

		if (account.getBalance().compareTo(amount) < 0) {
			throw new InvalidTransactionException("Insufficient balance for withdrawal");

		}

		if (!account.getClient().getEmail().equals(email)) {
			throw new InvalidTransactionException("Invalid withdrawal: it's not your account!");

		}

		account.setBalance(account.getBalance().subtract(amount));
		return accountRepository.save(account);

	}

	public BigDecimal getBalance(String email) {
		Account account = findAccountByClientEmail(email);
		return account.getBalance();
	}

}
