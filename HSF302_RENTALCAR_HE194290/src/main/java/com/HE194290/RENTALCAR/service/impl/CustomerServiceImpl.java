package com.HE194290.RENTALCAR.service.impl;

import com.HE194290.RENTALCAR.dto.request.CustomerRequest;
import com.HE194290.RENTALCAR.dto.response.CustomerResponse;
import com.HE194290.RENTALCAR.entity.Account;
import com.HE194290.RENTALCAR.entity.Customer;
import com.HE194290.RENTALCAR.repository.AccountRepository;
import com.HE194290.RENTALCAR.repository.CarRentalRepository;
import com.HE194290.RENTALCAR.repository.CustomerRepository;
import com.HE194290.RENTALCAR.service.AccountService;
import com.HE194290.RENTALCAR.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	private final AccountRepository accountRepository;
	private final CarRentalRepository carRentalRepository;
	private final AccountService accountService;

	public CustomerServiceImpl(CustomerRepository customerRepository, AccountRepository accountRepository,
			CarRentalRepository carRentalRepository, AccountService accountService) {
		this.customerRepository = customerRepository;
		this.accountRepository = accountRepository;
		this.carRentalRepository = carRentalRepository;
		this.accountService = accountService;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerResponse> findAll() {
		return customerRepository.findAll().stream().map(this::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerResponse get(Integer id) {
		return toDto(find(id));
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerResponse getByAccountId(Integer accountId) {
		return toDto(customerRepository.findByAccountAccountId(accountId)
				.orElseThrow(() -> new IllegalArgumentException("Customer profile not found")));
	}

	@Override
	@Transactional(readOnly = true)
	public CustomerRequest getRequest(Integer id) {
		Customer c = find(id);
		CustomerRequest r = new CustomerRequest();
		r.setCustomerId(c.getCustomerId());
		r.setFullName(c.getFullName());
		r.setMobile(c.getMobile());
		r.setBirthday(c.getBirthday());
		r.setIdentityCard(c.getIdentityCard());
		r.setLicenceNumber(c.getLicenceNumber());
		r.setLicenceDate(c.getLicenceDate());
		r.setAccountName(c.getAccount().getAccountName());
		r.setEmail(c.getAccount().getEmail());
		return r;
	}

	/** Tạo account (role Customer) cùng customer; dùng cho đăng ký và admin thêm mới. */
	@Override
	public CustomerResponse create(CustomerRequest request) {
		if (request.getPassword() == null || request.getPassword().length() < 6) {
			throw new IllegalArgumentException("Password must be at least 6 characters");
		}
		String email = request.getEmail().trim();
		if (accountRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("Email already exists");
		}
		Account account = new Account();
		account.setAccountName(request.getAccountName().trim());
		account.setEmail(email);
		account.setPassword(accountService.encode(request.getPassword()));
		account.setRole(Account.CUSTOMER);

		Customer c = new Customer();
		apply(c, request);
		c.setAccount(account);
		return toDto(customerRepository.save(c));
	}

	@Override
	public CustomerResponse update(CustomerRequest request) {
		Customer c = find(request.getCustomerId());
		Account account = c.getAccount();
		String email = request.getEmail().trim();
		if (accountRepository.existsByEmailAndAccountIdNot(email, account.getAccountId())) {
			throw new IllegalArgumentException("Email already exists");
		}
		account.setAccountName(request.getAccountName().trim());
		account.setEmail(email);
		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			if (request.getPassword().length() < 6) {
				throw new IllegalArgumentException("Password must be at least 6 characters");
			}
			account.setPassword(accountService.encode(request.getPassword()));
		}
		apply(c, request);
		return toDto(customerRepository.save(c));
	}

	/** Không cho xóa customer đã có giao dịch thuê xe để giữ lịch sử. */
	@Override
	public void delete(Integer id) {
		if (carRentalRepository.existsByCustomerCustomerId(id)) {
			throw new IllegalStateException("Cannot delete: this customer already has rental transactions");
		}
		customerRepository.deleteById(id);
	}

	private Customer find(Integer id) {
		return customerRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
	}

	private CustomerResponse toDto(Customer c) {
		CustomerResponse r = new CustomerResponse();
		r.setCustomerId(c.getCustomerId());
		r.setFullName(c.getFullName());
		r.setMobile(c.getMobile());
		r.setBirthday(c.getBirthday());
		r.setIdentityCard(c.getIdentityCard());
		r.setLicenceNumber(c.getLicenceNumber());
		r.setLicenceDate(c.getLicenceDate());
		r.setAccountName(c.getAccount().getAccountName());
		r.setEmail(c.getAccount().getEmail());
		return r;
	}

	private void apply(Customer c, CustomerRequest r) {
		c.setFullName(r.getFullName().trim());
		c.setMobile(r.getMobile().trim());
		c.setBirthday(r.getBirthday());
		c.setIdentityCard(r.getIdentityCard().trim());
		c.setLicenceNumber(r.getLicenceNumber().trim());
		c.setLicenceDate(r.getLicenceDate());
	}
}
