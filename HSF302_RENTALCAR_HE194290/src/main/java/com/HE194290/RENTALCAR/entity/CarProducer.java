package com.HE194290.RENTALCAR.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "car_producer")
public class CarProducer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "producer_id")
	private Integer producerId;

	@Column(name = "producer_name", nullable = false, length = 100)
	private String producerName;

	@Column(name = "address", nullable = false, length = 200)
	private String address;

	@Column(name = "country", nullable = false, length = 100)
	private String country;

	public CarProducer() {}

	public CarProducer(String producerName, String address, String country) {
		this.producerName = producerName;
		this.address = address;
		this.country = country;
	}

	public Integer getProducerId() { return producerId; }
	public void setProducerId(Integer producerId) { this.producerId = producerId; }
	public String getProducerName() { return producerName; }
	public void setProducerName(String producerName) { this.producerName = producerName; }
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }
	public String getCountry() { return country; }
	public void setCountry(String country) { this.country = country; }
}
