package com.HE194290.RENTALCAR.dto.response;

import java.time.LocalDate;

/** Dữ liệu customer để hiển thị; không chứa password. */
public class CustomerResponse {

	private Integer customerId;
	private String fullName;
	private String mobile;
	private LocalDate birthday;
	private String identityCard;
	private String licenceNumber;
	private LocalDate licenceDate;
	private String accountName;
	private String email;

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
}
