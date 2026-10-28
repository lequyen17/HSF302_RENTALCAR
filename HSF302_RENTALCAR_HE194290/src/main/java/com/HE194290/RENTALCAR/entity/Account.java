package com.HE194290.RENTALCAR.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "account")
public class Account {

	public static final String ADMIN = "Admin";
	public static final String CUSTOMER = "Customer";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private Integer accountId;

	@Column(name = "account_name", nullable = false, length = 100)
	private String accountName;

	@Column(name = "email", nullable = false, unique = true, columnDefinition = "varchar(200)")
	private String email;

	@Column(name = "password", nullable = false, columnDefinition = "varchar(200)")
	private String password;

	@Column(name = "role", nullable = false, length = 10)
	private String role;

	public Integer getAccountId() { return accountId; }
	public void setAccountId(Integer accountId) { this.accountId = accountId; }
	public String getAccountName() { return accountName; }
	public void setAccountName(String accountName) { this.accountName = accountName; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }
}
