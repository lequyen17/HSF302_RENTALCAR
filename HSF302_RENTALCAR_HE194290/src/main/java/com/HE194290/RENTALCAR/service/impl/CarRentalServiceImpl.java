package com.HE194290.RENTALCAR.service.impl;

import com.HE194290.RENTALCAR.dto.response.CarRentalResponse;
import com.HE194290.RENTALCAR.dto.response.ReportResponse;
import com.HE194290.RENTALCAR.entity.Car;
import com.HE194290.RENTALCAR.entity.CarRental;
import com.HE194290.RENTALCAR.entity.Customer;
import com.HE194290.RENTALCAR.repository.CarRentalRepository;
import com.HE194290.RENTALCAR.repository.CarRepository;
import com.HE194290.RENTALCAR.repository.CustomerRepository;
import com.HE194290.RENTALCAR.repository.ReviewRepository;
import com.HE194290.RENTALCAR.service.CarRentalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Tình trạng "xe đang được thuê" không lưu ở Car.status mà được suy ra từ các giao dịch chưa hủy theo khoảng ngày,
 * nên cùng một xe vẫn thuê được ở những khoảng ngày không trùng nhau.
 */
@Service
@Transactional
public class CarRentalServiceImpl implements CarRentalService {

	private static final int NO_RENTAL = -1;

	private final CarRentalRepository rentalRepository;
	private final CarRepository carRepository;
	private final CustomerRepository customerRepository;
	private final ReviewRepository reviewRepository;

	public CarRentalServiceImpl(CarRentalRepository rentalRepository, CarRepository carRepository,
			CustomerRepository customerRepository, ReviewRepository reviewRepository) {
		this.rentalRepository = rentalRepository;
		this.carRepository = carRepository;
		this.customerRepository = customerRepository;
		this.reviewRepository = reviewRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CarRentalResponse> findAll() {
		return rentalRepository.findAllByOrderByPickupDateDesc().stream().map(this::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<CarRentalResponse> findByCustomer(Integer customerId) {
		return rentalRepository.findByCustomerCustomerIdOrderByPickupDateDesc(customerId).stream().map(this::toDto)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CarRentalResponse get(Integer id) {
		return toDto(find(id));
	}

	@Override
	@Transactional(readOnly = true)
	public ReportResponse report(LocalDate start, LocalDate end) {
		if (start == null || end == null) {
			throw new IllegalArgumentException("Start date and end date are required");
		}
		if (end.isBefore(start)) {
			throw new IllegalArgumentException("End date must not be before start date");
		}
		List<CarRental> all = rentalRepository.findByPickupDateBetweenOrderByPickupDateDesc(start, end);
		List<CarRental> billable = all.stream().filter(r -> !CarRental.CANCELLED.equals(r.getStatus())).toList();

		ReportResponse res = new ReportResponse();
		res.setStartDate(start);
		res.setEndDate(end);
		res.setRentals(all.stream().map(this::toDto).toList());
		res.setRevenue(billable.stream().map(CarRental::getRentPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
		res.setActiveCount(all.stream().filter(r -> CarRental.ACTIVE.equals(r.getStatus())).count());
		res.setCompletedCount(all.stream().filter(r -> CarRental.COMPLETED.equals(r.getStatus())).count());
		res.setCancelledCount(all.stream().filter(r -> CarRental.CANCELLED.equals(r.getStatus())).count());
		return res;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isCarAvailable(Integer carId, LocalDate start, LocalDate end) {
		Car car = carRepository.findById(carId).orElse(null);
		if (car == null || Car.INACTIVE.equals(car.getStatus())) {
			return false;
		}
		return !rentalRepository.existsOverlap(carId, start, end, NO_RENTAL);
	}

	/** Một khách thuê nhiều xe: mỗi xe là một dòng car_rental cùng khoảng ngày. */
	@Override
	public List<CarRentalResponse> create(Integer customerId, List<Integer> carIds, LocalDate pickup, LocalDate ret) {
		if (carIds == null || carIds.isEmpty()) {
			throw new IllegalArgumentException("Please select at least one car");
		}
		validateDates(pickup, ret);
		if (pickup.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Pickup date cannot be in the past");
		}
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new IllegalArgumentException("Customer not found"));

		List<CarRentalResponse> result = new ArrayList<>();
		for (Integer carId : carIds.stream().distinct().toList()) {
			Car car = carRepository.findById(carId)
					.orElseThrow(() -> new IllegalArgumentException("Car not found: " + carId));
			if (Car.INACTIVE.equals(car.getStatus())) {
				throw new IllegalArgumentException("Car '" + car.getCarName() + "' is not available for rent");
			}
			if (rentalRepository.existsOverlap(carId, pickup, ret, NO_RENTAL)) {
				throw new IllegalArgumentException(
						"Car '" + car.getCarName() + "' is already booked between " + pickup + " and " + ret);
			}
			CarRental r = new CarRental();
			r.setCustomer(customer);
			r.setCar(car);
			r.setPickupDate(pickup);
			r.setReturnDate(ret);
			r.setRentPrice(price(car, pickup, ret));
			r.setStatus(CarRental.ACTIVE);
			result.add(toDto(rentalRepository.save(r)));
		}
		return result;
	}

	@Override
	public CarRentalResponse update(Integer id, LocalDate pickup, LocalDate ret, String status) {
		validateDates(pickup, ret);
		if (!List.of(CarRental.ACTIVE, CarRental.COMPLETED, CarRental.CANCELLED).contains(status)) {
			throw new IllegalArgumentException("Invalid status");
		}
		CarRental r = find(id);
		if (!CarRental.CANCELLED.equals(status)
				&& rentalRepository.existsOverlap(r.getCar().getCarId(), pickup, ret, id)) {
			throw new IllegalArgumentException(
					"Car '" + r.getCar().getCarName() + "' is already booked between " + pickup + " and " + ret);
		}
		r.setPickupDate(pickup);
		r.setReturnDate(ret);
		r.setRentPrice(price(r.getCar(), pickup, ret));
		r.setStatus(status);
		return toDto(rentalRepository.save(r));
	}

	/** Khách hàng chỉ được hủy giao dịch của chính mình khi còn Active. */
	@Override
	public void cancelByCustomer(Integer rentId, Integer customerId) {
		CarRental r = find(rentId);
		if (!r.getCustomer().getCustomerId().equals(customerId)) {
			throw new IllegalArgumentException("This transaction does not belong to you");
		}
		if (!CarRental.ACTIVE.equals(r.getStatus())) {
			throw new IllegalStateException("Only active transactions can be cancelled");
		}
		r.setStatus(CarRental.CANCELLED);
		rentalRepository.save(r);
	}

	@Override
	public void delete(Integer id) {
		CarRental r = find(id);
		reviewRepository.deleteAll(reviewRepository.findByCarRentalCarRentId(id));
		rentalRepository.delete(r);
	}

	private void validateDates(LocalDate pickup, LocalDate ret) {
		if (pickup == null || ret == null) {
			throw new IllegalArgumentException("Pickup date and return date are required");
		}
		if (!ret.isAfter(pickup)) {
			throw new IllegalArgumentException("Return date must be after pickup date");
		}
	}

	private BigDecimal price(Car car, LocalDate pickup, LocalDate ret) {
		long days = ChronoUnit.DAYS.between(pickup, ret);
		return car.getRentPrice().multiply(BigDecimal.valueOf(days));
	}

	private CarRental find(Integer id) {
		return rentalRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Rental not found: " + id));
	}

	private CarRentalResponse toDto(CarRental r) {
		CarRentalResponse d = new CarRentalResponse();
		d.setCarRentId(r.getCarRentId());
		d.setCustomerId(r.getCustomer().getCustomerId());
		d.setCustomerName(r.getCustomer().getFullName());
		d.setCarId(r.getCar().getCarId());
		d.setCarName(r.getCar().getCarName());
		d.setPickupDate(r.getPickupDate());
		d.setReturnDate(r.getReturnDate());
		d.setRentPrice(r.getRentPrice());
		d.setStatus(r.getStatus());
		return d;
	}
}
