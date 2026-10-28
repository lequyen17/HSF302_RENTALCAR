package com.HE194290.RENTALCAR.dto.request;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/** Request dùng chung cho đăng ký, admin quản lý customer và chỉnh sửa profile (gồm cả thông tin account). */
public class CustomerRequest {

	private Integer customerId;

	@NotBlank(message = "Full name is required")
	@Size(max = 200)
	private String fullName;

	@NotBlank(message = "Mobile is required")
	@Pattern(regexp = "^[0-9+]{8,15}$", message = "Mobile must be 8-15 digits")
	private String mobile;

	@NotNull(message = "Birthday is required")
	@Past(message = "Birthday must be in the past")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate birthday;

	@NotBlank(message = "Identity card is required")
	@Size(max = 20)
	private String identityCard;

	@NotBlank(message = "Licence number is required")
	@Size(max = 20)
	private String licenceNumber;

	@NotNull(message = "Licence date is required")
	@PastOrPresent(message = "Licence date cannot be in the future")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate licenceDate;

	@NotBlank(message = "Account name is required")
	@Size(max = 100)
	private String accountName;

	@NotBlank(message = "Email is required")
	@Email(message = "Email is invalid")
	@Size(max = 200)
	private String email;

	/** Bắt buộc khi tạo mới; khi sửa để trống nghĩa là giữ mật khẩu cũ. */
	@Size(max = 100)
	private String password;

	public Integer getCustomerId() { return customerId; }
	public void setCustomerId(Integer customerId) { this.customerId = customerId; }
	public String getFullName() { return fullName; }
	public void setFullName(String fullName) { this.fullName = fullName; }
	public String getMobile() { return mobile; }
	public void setMobile(String mobile) { this.mobile = mobile; }
	public LocalDate getBirthday() { return birthday; }
	public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
	public String getIdentityCard() { return identityCard; }
	public void setIdentityCard(String identityCard) { this.identityCard = identityCard; }
	public String getLicenceNumber() { return licenceNumber; }
	public void setLicenceNumber(String licenceNumber) { this.licenceNumber = licenceNumber; }
	public LocalDate getLicenceDate() { return licenceDate; }
	public void setLicenceDate(LocalDate licenceDate) { this.licenceDate = licenceDate; }
	public String getAccountName() { return accountName; }
	public void setAccountName(String accountName) { this.accountName = accountName; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
}
