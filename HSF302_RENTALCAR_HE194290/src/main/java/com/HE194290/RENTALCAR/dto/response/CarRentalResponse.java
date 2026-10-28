package com.HE194290.RENTALCAR.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CarRentalResponse {

	private Integer carRentId;
	private Integer customerId;
	private String customerName;
	private Integer carId;
	private String carName;
	private LocalDate pickupDate;
	private LocalDate returnDate;
	private BigDecimal rentPrice;
	private String status;

	public Integer getCarRentId() { return carRentId; }
	public void setCarRentId(Integer carRentId) { this.carRentId = carRentId; }
	public Integer getCustomerId() { return customerId; }
	public void setCustomerId(Integer customerId) { this.customerId = customerId; }
	public String getCustomerName() { return customerName; }
	public void setCustomerName(String customerName) { this.customerName = customerName; }
	public Integer getCarId() { return carId; }
	public void setCarId(Integer carId) { this.carId = carId; }
	public String getCarName() { return carName; }
	public void setCarName(String carName) { this.carName = carName; }
	public LocalDate getPickupDate() { return pickupDate; }
	public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }
	public LocalDate getReturnDate() { return returnDate; }
	public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
	public BigDecimal getRentPrice() { return rentPrice; }
	public void setRentPrice(BigDecimal rentPrice) { this.rentPrice = rentPrice; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}
