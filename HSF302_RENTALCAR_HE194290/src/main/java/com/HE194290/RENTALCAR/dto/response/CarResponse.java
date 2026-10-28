package com.HE194290.RENTALCAR.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CarResponse {

	private Integer carId;
	private String carName;
	private Integer carModelYear;
	private String color;
	private Integer capacity;
	private String description;
	private LocalDate importDate;
	private Integer producerId;
	private String producerName;
	private BigDecimal rentPrice;
	private String imageUrl;
	private String status;
	private Double avgRating;
	private long reviewCount;

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
	public String getProducerName() { return producerName; }
	public void setProducerName(String producerName) { this.producerName = producerName; }
	public BigDecimal getRentPrice() { return rentPrice; }
	public void setRentPrice(BigDecimal rentPrice) { this.rentPrice = rentPrice; }
	public String getImageUrl() { return imageUrl; }
	public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
	public Double getAvgRating() { return avgRating; }
	public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
	public long getReviewCount() { return reviewCount; }
	public void setReviewCount(long reviewCount) { this.reviewCount = reviewCount; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}
