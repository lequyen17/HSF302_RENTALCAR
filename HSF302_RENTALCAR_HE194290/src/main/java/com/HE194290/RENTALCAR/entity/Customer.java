package com.HE194290.RENTALCAR.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "customer")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id")
	private Integer customerId;

	@Column(name = "full_name", nullable = false, length = 200)
	private String fullName;

	@Column(name = "mobile", nullable = false, columnDefinition = "varchar(15)")
	private String mobile;

	@Column(name = "birthday", nullable = false)
	private LocalDate birthday;

	@Column(name = "identity_card", nullable = false, columnDefinition = "varchar(20)")
	private String identityCard;

	@Column(name = "licence_number", nullable = false, columnDefinition = "varchar(20)")
	private String licenceNumber;

	@Column(name = "licence_date", nullable = false)
	private LocalDate licenceDate;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "account_id", nullable = false, unique = true)
	private Account account;

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
	public Account getAccount() { return account; }
	public void setAccount(Account account) { this.account = account; }
}
