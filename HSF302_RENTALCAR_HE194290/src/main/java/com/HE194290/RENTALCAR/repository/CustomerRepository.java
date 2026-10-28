package com.HE194290.RENTALCAR.repository;

import com.HE194290.RENTALCAR.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	Optional<Customer> findByAccountAccountId(Integer accountId);

	List<Customer> findByFullNameContainingIgnoreCase(String keyword);
}
