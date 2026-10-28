package com.HE194290.RENTALCAR.repository;

import com.HE194290.RENTALCAR.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	boolean existsByCarRentalCarRentId(Integer carRentId);

	List<Review> findByCarRentalCarRentId(Integer carRentId);

	List<Review> findAllByOrderByIdDesc();

	List<Review> findByCarRentalCarCarIdOrderByIdDesc(Integer carId);

	List<Review> findByCarRentalCustomerCustomerIdOrderByIdDesc(Integer customerId);

	/** Mỗi dòng: [carId, điểm trung bình, số đánh giá]. */
	@Query("select r.carRental.car.carId, avg(r.reviewStar), count(r) from Review r group by r.carRental.car.carId")
	List<Object[]> ratingByCar();

	@Query("select avg(r.reviewStar) from Review r")
	Double averageStar();
}
