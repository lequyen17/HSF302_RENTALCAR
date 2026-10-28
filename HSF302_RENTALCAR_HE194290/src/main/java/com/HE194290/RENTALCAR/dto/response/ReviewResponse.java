package com.HE194290.RENTALCAR.dto.response;

public class ReviewResponse {

	private Integer id;
	private Integer carRentId;
	private Integer carId;
	private String carName;
	private String customerName;
	private int reviewStar;
	private String comment;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Integer getCarRentId() { return carRentId; }
	public void setCarRentId(Integer carRentId) { this.carRentId = carRentId; }
	public Integer getCarId() { return carId; }
	public void setCarId(Integer carId) { this.carId = carId; }
	public String getCarName() { return carName; }
	public void setCarName(String carName) { this.carName = carName; }
	public String getCustomerName() { return customerName; }
	public void setCustomerName(String customerName) { this.customerName = customerName; }
	public int getReviewStar() { return reviewStar; }
	public void setReviewStar(int reviewStar) { this.reviewStar = reviewStar; }
	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }
}
