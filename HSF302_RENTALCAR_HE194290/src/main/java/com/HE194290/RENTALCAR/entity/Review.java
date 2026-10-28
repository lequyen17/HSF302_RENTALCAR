package com.HE194290.RENTALCAR.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "review")
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "car_rent_id", nullable = false)
	private CarRental carRental;

	@Column(name = "review_star", nullable = false)
	private int reviewStar;

	@Column(name = "comment", nullable = false, length = 500)
	private String comment;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public CarRental getCarRental() { return carRental; }
	public void setCarRental(CarRental carRental) { this.carRental = carRental; }
	public int getReviewStar() { return reviewStar; }
	public void setReviewStar(int reviewStar) { this.reviewStar = reviewStar; }
	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }
}
