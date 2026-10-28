package com.HE194290.RENTALCAR.service;

import com.HE194290.RENTALCAR.dto.response.ReviewResponse;

import java.util.List;
import java.util.Map;

public interface ReviewService {

	List<ReviewResponse> findAll();

	List<ReviewResponse> findByCar(Integer carId);

	List<ReviewResponse> findByCustomer(Integer customerId);

	/** Đánh giá của khách theo từng giao dịch: key là carRentId. */
	Map<Integer, ReviewResponse> findByCustomerKeyedByRental(Integer customerId);

	/** Lấy đánh giá của chính khách đó, ném IllegalArgumentException nếu không tồn tại hoặc không thuộc về khách. */
	ReviewResponse getOwned(Integer reviewId, Integer customerId);

	/** Tên xe của giao dịch, kiểm tra giao dịch thuộc về khách. Dùng để hiển thị form đánh giá. */
	String carNameOfRental(Integer rentId, Integer customerId);

	void create(Integer rentId, Integer customerId, int star, String comment);

	void update(Integer reviewId, Integer customerId, int star, String comment);

	void deleteByCustomer(Integer reviewId, Integer customerId);

	void deleteByAdmin(Integer reviewId);
}
