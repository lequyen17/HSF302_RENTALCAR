package com.HE194290.RENTALCAR.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "car")
public class Car {

	public static final String AVAILABLE = "Available";
	public static final String INACTIVE = "Inactive";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "car_id")
	private Integer carId;

	@NotBlank(message = "Car name is required")
	@Size(max = 200)
	@Column(name = "car_name", nullable = false, length = 200)
	private String carName;

	@NotNull(message = "Model year is required")
	@Min(value = 1900, message = "Model year must be >= 1900")
	@Max(value = 2100, message = "Model year must be <= 2100")
	@Column(name = "car_model_year", nullable = false)
	private Integer carModelYear;

	@NotBlank(message = "Color is required")
	@Size(max = 50)
	@Column(name = "color", nullable = false, length = 50)
	private String color;

	@NotNull(message = "Capacity is required")
	@Min(value = 1, message = "Capacity must be >= 1")
	@Column(name = "capacity", nullable = false)
	private Integer capacity;

	@NotBlank(message = "Description is required")
	@Size(max = 1000)
	@Column(name = "description", nullable = false, length = 1000)
	private String description;

	@NotNull(message = "Import date is required")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@Column(name = "import_date", nullable = false)
	private LocalDate importDate;

	@NotNull(message = "Producer is required")
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "producer_id", nullable = false)
	private CarProducer producer;

	@NotNull(message = "Rent price is required")
	@DecimalMin(value = "0.01", message = "Rent price must be > 0")
	@Digits(integer = 8, fraction = 2)
	@Column(name = "rent_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal rentPrice;

	@Size(max = 500)
	@Column(name = "image_url", length = 500)
	private String imageUrl;

	@NotBlank(message = "Status is required")
	@Column(name = "status", nullable = false, length = 10)
	private String status = AVAILABLE;

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
	public CarProducer getProducer() { return producer; }
	public void setProducer(CarProducer producer) { this.producer = producer; }
	public BigDecimal getRentPrice() { return rentPrice; }
	public void setRentPrice(BigDecimal rentPrice) { this.rentPrice = rentPrice; }
	public String getImageUrl() { return imageUrl; }
	public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}
