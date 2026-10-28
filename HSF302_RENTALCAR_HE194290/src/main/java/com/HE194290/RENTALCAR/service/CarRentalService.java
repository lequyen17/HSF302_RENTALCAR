package com.HE194290.RENTALCAR.service;

import com.HE194290.RENTALCAR.dto.response.CarRentalResponse;

import java.time.LocalDate;
import java.util.List;

public interface CarRentalService {

	List<CarRentalResponse> findAll();

	List<CarRentalResponse> findByCustomer(Integer customerId);

	CarRentalResponse get(Integer id);

	/** Báo cáo theo khoảng ngày nhận xe StartDate -> EndDate, sắp xếp giảm dần. */
	List<CarRentalResponse> report(LocalDate start, LocalDate end);

	/** Một khách thuê nhiều xe: mỗi xe là một dòng car_rental cùng khoảng ngày. */
	List<CarRentalResponse> create(Integer customerId, List<Integer> carIds, LocalDate pickup, LocalDate ret);

	CarRentalResponse update(Integer id, LocalDate pickup, LocalDate ret, String status);

	/** Khách hàng chỉ được hủy giao dịch của chính mình khi còn Active. */
	void cancelByCustomer(Integer rentId, Integer customerId);

	void delete(Integer id);

	void addReview(Integer rentId, Integer customerId, int star, String comment);

	boolean isReviewed(Integer rentId);
}
