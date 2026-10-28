package com.HE194290.RENTALCAR.repository;

import com.HE194290.RENTALCAR.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

	Optional<Account> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByEmailAndAccountIdNot(String email, Integer accountId);
}
