package com.HE194290.RENTALCAR.repository;

import com.HE194290.RENTALCAR.entity.CarRental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CarRentalRepository extends JpaRepository<CarRental, Integer> {

	boolean existsByCarCarId(Integer carId);

	boolean existsByCustomerCustomerId(Integer customerId);

	List<CarRental> findByCustomerCustomerIdOrderByPickupDateDesc(Integer customerId);

	List<CarRental> findAllByOrderByPickupDateDesc();

	List<CarRental> findByPickupDateBetweenOrderByPickupDateDesc(LocalDate start, LocalDate end);
}
