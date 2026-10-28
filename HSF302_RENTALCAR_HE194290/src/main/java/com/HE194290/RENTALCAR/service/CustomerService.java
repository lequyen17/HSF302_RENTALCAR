package com.HE194290.RENTALCAR.service;

import com.HE194290.RENTALCAR.dto.request.CustomerRequest;
import com.HE194290.RENTALCAR.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {

	List<CustomerResponse> findAll();

	CustomerResponse get(Integer id);

	CustomerResponse getByAccountId(Integer accountId);

	/** Request đã điền sẵn dữ liệu hiện tại (password để trống) để đổ vào form sửa. */
	CustomerRequest getRequest(Integer id);

	/** Tạo account (role Customer) cùng customer; dùng cho đăng ký và admin thêm mới. */
	CustomerResponse create(CustomerRequest request);

	CustomerResponse update(CustomerRequest request);

	/** Không cho xóa customer đã có giao dịch thuê xe để giữ lịch sử. */
	void delete(Integer id);
}
