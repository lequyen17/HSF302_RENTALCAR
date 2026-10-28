package com.HE194290.RENTALCAR.service;

import com.HE194290.RENTALCAR.dto.request.CarRequest;
import com.HE194290.RENTALCAR.dto.response.CarProducerResponse;
import com.HE194290.RENTALCAR.dto.response.CarResponse;

import java.util.List;

public interface CarService {

	List<CarResponse> search(String keyword);

	List<CarResponse> findAvailable();

	CarResponse get(Integer id);

	/** Request đã điền sẵn dữ liệu hiện tại để đổ vào form sửa. */
	CarRequest getRequest(Integer id);

	List<CarProducerResponse> producers();

	/** Tạo mới khi carId null, ngược lại cập nhật xe có sẵn. */
	CarResponse save(CarRequest request);

	/**
	 * @return true nếu xóa hẳn; false nếu xe đã nằm trong giao dịch thuê nên chỉ đổi trạng thái sang Inactive.
	 */
	boolean delete(Integer id);
}
