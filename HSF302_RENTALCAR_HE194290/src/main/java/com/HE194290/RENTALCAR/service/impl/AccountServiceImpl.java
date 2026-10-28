package com.HE194290.RENTALCAR.service.impl;

import com.HE194290.RENTALCAR.entity.Account;
import com.HE194290.RENTALCAR.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.HE194290.RENTALCAR.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	public AccountServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Optional<Account> login(String email, String rawPassword) {
		return accountRepository.findByEmail(email == null ? "" : email.trim())
				.filter(a -> passwordEncoder.matches(rawPassword, a.getPassword()));
	}

	@Override
	public String encode(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}
}
