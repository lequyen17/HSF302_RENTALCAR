package com.HE194290.RENTALCAR.repository;

import com.HE194290.RENTALCAR.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	boolean existsByCarRentalCarRentId(Integer carRentId);

	List<Review> findByCarRentalCarRentId(Integer carRentId);
}
