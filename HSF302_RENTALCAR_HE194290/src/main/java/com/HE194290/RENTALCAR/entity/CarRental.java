package com.HE194290.RENTALCAR.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "car_rental")
public class CarRental {

	public static final String ACTIVE = "Active";
	public static final String COMPLETED = "Completed";
	public static final String CANCELLED = "Cancelled";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "car_rent_id")
	private Integer carRentId;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "car_id", nullable = false)
	private Car car;

	@Column(name = "pickup_date", nullable = false)
	private LocalDate pickupDate;

	@Column(name = "return_date", nullable = false)
	private LocalDate returnDate;

	@Column(name = "rent_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal rentPrice;

	@Column(name = "status", nullable = false, length = 10)
	private String status = ACTIVE;

	public Integer getCarRentId() { return carRentId; }
	public void setCarRentId(Integer carRentId) { this.carRentId = carRentId; }
	public Customer getCustomer() { return customer; }
	public void setCustomer(Customer customer) { this.customer = customer; }
	public Car getCar() { return car; }
	public void setCar(Car car) { this.car = car; }
	public LocalDate getPickupDate() { return pickupDate; }
	public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }
	public LocalDate getReturnDate() { return returnDate; }
	public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
	public BigDecimal getRentPrice() { return rentPrice; }
	public void setRentPrice(BigDecimal rentPrice) { this.rentPrice = rentPrice; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}
