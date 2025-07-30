package com.bank.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bank.model.UserLogin;
import com.bank.repository.UserLoginRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserLoginRepository loginRepository;

	public UserDetailsServiceImpl(UserLoginRepository loginRepository) {
		this.loginRepository = loginRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserLogin user = loginRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found " + email));

		return org.springframework.security.core.userdetails.User.builder().username(user.getEmail())
				.password(user.getpassword()).roles("USER").build();
	}
}
