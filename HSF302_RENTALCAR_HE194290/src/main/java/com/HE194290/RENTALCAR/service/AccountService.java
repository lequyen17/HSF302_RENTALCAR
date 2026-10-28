package com.HE194290.RENTALCAR.service;

import com.HE194290.RENTALCAR.entity.Account;

import java.util.Optional;

public interface AccountService {

	Optional<Account> login(String email, String rawPassword);

	String encode(String rawPassword);
}
