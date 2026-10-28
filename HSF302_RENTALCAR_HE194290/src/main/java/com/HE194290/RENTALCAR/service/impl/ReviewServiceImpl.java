package com.HE194290.RENTALCAR.service.impl;

import com.HE194290.RENTALCAR.dto.response.ReviewResponse;
import com.HE194290.RENTALCAR.entity.CarRental;
import com.HE194290.RENTALCAR.entity.Review;
import com.HE194290.RENTALCAR.repository.CarRentalRepository;
import com.HE194290.RENTALCAR.repository.ReviewRepository;
import com.HE194290.RENTALCAR.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final CarRentalRepository rentalRepository;

	public ReviewServiceImpl(ReviewRepository reviewRepository, CarRentalRepository rentalRepository) {
		this.reviewRepository = reviewRepository;
		this.rentalRepository = rentalRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReviewResponse> findAll() {
		return reviewRepository.findAllByOrderByIdDesc().stream().map(this::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReviewResponse> findByCar(Integer carId) {
		return reviewRepository.findByCarRentalCarCarIdOrderByIdDesc(carId).stream().map(this::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReviewResponse> findByCustomer(Integer customerId) {
		return reviewRepository.findByCarRentalCustomerCustomerIdOrderByIdDesc(customerId).stream().map(this::toDto)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Map<Integer, ReviewResponse> findByCustomerKeyedByRental(Integer customerId) {
		Map<Integer, ReviewResponse> map = new LinkedHashMap<>();
		for (ReviewResponse r : findByCustomer(customerId)) {
			map.put(r.getCarRentId(), r);
		}
		return map;
	}

	@Override
	@Transactional(readOnly = true)
	public ReviewResponse getOwned(Integer reviewId, Integer customerId) {
		return toDto(findOwned(reviewId, customerId));
	}

	@Override
	@Transactional(readOnly = true)
	public String carNameOfRental(Integer rentId, Integer customerId) {
		CarRental rental = findOwnedRental(rentId, customerId);
		if (!CarRental.COMPLETED.equals(rental.getStatus())) {
			throw new IllegalStateException("You can only review completed transactions");
		}
		if (reviewRepository.existsByCarRentalCarRentId(rentId)) {
			throw new IllegalStateException("This transaction has already been reviewed");
		}
		return rental.getCar().getCarName();
	}

	@Override
	public void create(Integer rentId, Integer customerId, int star, String comment) {
		CarRental rental = findOwnedRental(rentId, customerId);
		if (!CarRental.COMPLETED.equals(rental.getStatus())) {
			throw new IllegalStateException("You can only review completed transactions");
		}
		if (reviewRepository.existsByCarRentalCarRentId(rentId)) {
			throw new IllegalStateException("This transaction has already been reviewed");
		}
		validate(star, comment);
		Review review = new Review();
		review.setCarRental(rental);
		review.setReviewStar(star);
		review.setComment(comment.trim());
		reviewRepository.save(review);
	}

	@Override
	public void update(Integer reviewId, Integer customerId, int star, String comment) {
		Review review = findOwned(reviewId, customerId);
		validate(star, comment);
		review.setReviewStar(star);
		review.setComment(comment.trim());
		reviewRepository.save(review);
	}

	@Override
	public void deleteByCustomer(Integer reviewId, Integer customerId) {
		reviewRepository.delete(findOwned(reviewId, customerId));
	}

	@Override
	public void deleteByAdmin(Integer reviewId) {
		if (!reviewRepository.existsById(reviewId)) {
			throw new IllegalArgumentException("Review not found: " + reviewId);
		}
		reviewRepository.deleteById(reviewId);
	}

	private void validate(int star, String comment) {
		if (star < 1 || star > 5) {
			throw new IllegalArgumentException("Star rating must be 1-5");
		}
		if (comment == null || comment.isBlank() || comment.length() > 500) {
			throw new IllegalArgumentException("Comment is required (max 500 characters)");
		}
	}

	private CarRental findOwnedRental(Integer rentId, Integer customerId) {
		CarRental rental = rentalRepository.findById(rentId)
				.orElseThrow(() -> new IllegalArgumentException("Rental not found: " + rentId));
		if (!rental.getCustomer().getCustomerId().equals(customerId)) {
			throw new IllegalArgumentException("This transaction does not belong to you");
		}
		return rental;
	}

	private Review findOwned(Integer reviewId, Integer customerId) {
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));
		if (!review.getCarRental().getCustomer().getCustomerId().equals(customerId)) {
			throw new IllegalArgumentException("This review does not belong to you");
		}
		return review;
	}

	private ReviewResponse toDto(Review r) {
		ReviewResponse d = new ReviewResponse();
		d.setId(r.getId());
		d.setCarRentId(r.getCarRental().getCarRentId());
		d.setCarId(r.getCarRental().getCar().getCarId());
		d.setCarName(r.getCarRental().getCar().getCarName());
		d.setCustomerName(r.getCarRental().getCustomer().getFullName());
		d.setReviewStar(r.getReviewStar());
		d.setComment(r.getComment());
		return d;
	}
}
