package com.HE194290.RENTALCAR.repository;

import com.HE194290.RENTALCAR.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {

	List<Car> findByStatus(String status);
}
