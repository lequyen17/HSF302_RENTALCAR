package com.HE194290.RENTALCAR.service;

import com.HE194290.RENTALCAR.dto.request.CarRequest;
import com.HE194290.RENTALCAR.dto.response.CarProducerResponse;
import com.HE194290.RENTALCAR.dto.response.CarResponse;

import java.time.LocalDate;
import java.util.List;

public interface CarService {

	/** Dành cho Admin: toàn bộ xe, gồm cả xe Inactive. Tìm kiếm, sắp xếp, phân trang do DataTables xử lý ở trình duyệt. */
	List<CarResponse> findAll();

	/**
	 * Xe có thể thuê trong khoảng [start, end): không Inactive và không trùng lịch với giao dịch nào chưa hủy.
	 * Nếu start/end null thì không lọc theo thời gian. Các tham số còn lại có thể null.
	 */
	List<CarResponse> findAvailable(LocalDate start, LocalDate end, String keyword, Integer producerId,
			Integer minSeats);

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
