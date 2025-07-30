package com.bank.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.config.JwtService;
import com.bank.config.UserDetailsServiceImpl;

import com.bank.model.Client;
import com.bank.model.CurrentAccount;
import com.bank.model.PasswordResetToken;
import com.bank.model.UserLogin;
import com.bank.repository.AccountRepository;
import com.bank.repository.ClientRepository;
import com.bank.repository.PasswordResetTokenRepository;
import com.bank.repository.UserLoginRepository;

@Service
public class UserLoginService {

	private final ClientRepository clientRepository;
	private final UserLoginRepository userLoginRepository;
	private final PasswordResetTokenRepository tokenRepository;
	private final EmailService emailService;
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final UserDetailsServiceImpl userDetailsService;
	private final JwtService jwtService;

	public UserLoginService(ClientRepository clientRepository, UserLoginRepository userLoginRepository,
			PasswordResetTokenRepository tokenRepository, EmailService emailService,
			AccountRepository accountRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService,
			JwtService jwtService) {

		this.clientRepository = clientRepository;
		this.userLoginRepository = userLoginRepository;
		this.tokenRepository = tokenRepository;
		this.emailService = emailService;
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtService = jwtService;
	}

	public String forgotPassword(String email) {
		Optional<Client> clientOpt = clientRepository.findByEmail(email);
		if (clientOpt.isEmpty()) {
			return "Error: Email is not registered";
		}

		String token = UUID.randomUUID().toString();
		LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setEmail(email);
		resetToken.setToken(token);
		resetToken.setExpiryDate(expiryDate);

		tokenRepository.save(resetToken);

		String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;

		try {
			emailService.sendEmail(email, "Password reset -", " Reset your password using the link: " + resetLink);
		} catch (Exception e) {
			return "Error: Failed to send email - " + e.getMessage();
		}

		return "Email sent successfully to: " + email;
	}

	public String resetPassword(String token, String newPassword) {
		Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
		if (tokenOpt.isEmpty()) {
			return "Error: Token invalid";
		}

		PasswordResetToken resetToken = tokenOpt.get();
		if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			return "Error: Token expired";
		}

		Optional<UserLogin> userLoginOpt = userLoginRepository.findByEmail(resetToken.getEmail());
		if (userLoginOpt.isEmpty()) {
			return "Error: User not found";
		}

		if (newPassword == null || newPassword.trim().isEmpty()) {
			return "Error: Password cannot be empty";
		}

		UserLogin userLogin = userLoginOpt.get();

		userLogin.setpassword(passwordEncoder.encode(newPassword));
		userLoginRepository.save(userLogin);

		tokenRepository.delete(resetToken);

		return "Password reset successfully";
	}

	public String createPassword(String email, String password) {

		if (password == null || password.trim().isEmpty()) {
			throw new IllegalArgumentException("Password can not be empty");
		}

		Client client = clientRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("Client is not registered"));

		if (userLoginRepository.findByEmail(email).isPresent()) {
			return "Error: Email already registered. Please use another email";
		}

		UserLogin userLogin = new UserLogin();
		userLogin.setEmail(email);

		String encodedPassword = passwordEncoder.encode(password);
		userLogin.setpassword(encodedPassword);
		userLogin.setClient(client);
		userLoginRepository.save(userLogin);

		CurrentAccount currentAccount = new CurrentAccount();
		currentAccount.setClient(client);
		currentAccount.setBalance(BigDecimal.ZERO);
		currentAccount.setCreateDate(LocalDate.now());
		currentAccount.setNumber(generateAccountNumber());

		accountRepository.save(currentAccount);

		return "User and current account created successfully. Welcome!";
	}

	private String generateAccountNumber() {
		long randomPart = ((long) (Math.random() * 1_000_000_0000L));
		long timePart = System.currentTimeMillis() % 1_000_000_000L;
		return String.format("%09d%09d", randomPart, timePart);
	}

	public String login(String email, String password) {
		try {
			var authToken = new UsernamePasswordAuthenticationToken(email, password);
			authenticationManager.authenticate(authToken);
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);
			return jwtService.generateToken(userDetails);
		} catch (AuthenticationException e) {
			throw new IllegalArgumentException("User or password invalid");
		}
	}

}
