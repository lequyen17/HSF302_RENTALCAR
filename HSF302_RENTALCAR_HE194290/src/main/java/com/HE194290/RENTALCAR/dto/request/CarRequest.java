package com.HE194290.RENTALCAR.dto.request;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Request tạo/sửa xe; producer chỉ truyền id để service tự load entity. */
public class CarRequest {

	private Integer carId;

	@NotBlank(message = "Car name is required")
	@Size(max = 200)
	private String carName;

	@NotNull(message = "Model year is required")
	@Min(value = 1900, message = "Model year must be >= 1900")
	@Max(value = 2100, message = "Model year must be <= 2100")
	private Integer carModelYear;

	@NotBlank(message = "Color is required")
	@Size(max = 50)
	private String color;

	@NotNull(message = "Capacity is required")
	@Min(value = 1, message = "Capacity must be >= 1")
	private Integer capacity;

	@NotBlank(message = "Description is required")
	@Size(max = 1000)
	private String description;

	@NotNull(message = "Import date is required")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate importDate;

	@NotNull(message = "Producer is required")
	private Integer producerId;

	@NotNull(message = "Rent price is required")
	@DecimalMin(value = "0.01", message = "Rent price must be > 0")
	@Digits(integer = 8, fraction = 2)
	private BigDecimal rentPrice;

	@Size(max = 500)
	private String imageUrl;

	@NotBlank(message = "Status is required")
	private String status;

	public Integer getCarId() { return carId; }
	public void setCarId(Integer carId) { this.carId = carId; }
	public String getCarName() { return carName; }
	public void setCarName(String carName) { this.carName = carName; }
	public Integer getCarModelYear() { return carModelYear; }
	public void setCarModelYear(Integer carModelYear) { this.carModelYear = carModelYear; }
	public String getColor() { return color; }
	public void setColor(String color) { this.color = color; }
	public Integer getCapacity() { return capacity; }
	public void setCapacity(Integer capacity) { this.capacity = capacity; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public LocalDate getImportDate() { return importDate; }
	public void setImportDate(LocalDate importDate) { this.importDate = importDate; }
	public Integer getProducerId() { return producerId; }
	public void setProducerId(Integer producerId) { this.producerId = producerId; }
	public BigDecimal getRentPrice() { return rentPrice; }
	public void setRentPrice(BigDecimal rentPrice) { this.rentPrice = rentPrice; }
	public String getImageUrl() { return imageUrl; }
	public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}
