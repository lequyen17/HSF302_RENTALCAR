package com.HE194290.RENTALCAR.service.impl;

import com.HE194290.RENTALCAR.dto.request.CarRequest;
import com.HE194290.RENTALCAR.dto.response.CarProducerResponse;
import com.HE194290.RENTALCAR.dto.response.CarResponse;
import com.HE194290.RENTALCAR.entity.Car;
import com.HE194290.RENTALCAR.entity.CarProducer;
import com.HE194290.RENTALCAR.repository.CarProducerRepository;
import com.HE194290.RENTALCAR.repository.CarRentalRepository;
import com.HE194290.RENTALCAR.repository.CarRepository;
import com.HE194290.RENTALCAR.repository.ReviewRepository;
import com.HE194290.RENTALCAR.service.CarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class CarServiceImpl implements CarService {

	private final CarRepository carRepository;
	private final CarProducerRepository producerRepository;
	private final CarRentalRepository carRentalRepository;
	private final ReviewRepository reviewRepository;

	public CarServiceImpl(CarRepository carRepository, CarProducerRepository producerRepository,
			CarRentalRepository carRentalRepository, ReviewRepository reviewRepository) {
		this.carRepository = carRepository;
		this.producerRepository = producerRepository;
		this.carRentalRepository = carRentalRepository;
		this.reviewRepository = reviewRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CarResponse> findAll() {
		return toDtos(carRepository.findAll());
	}

	@Override
	@Transactional(readOnly = true)
	public List<CarResponse> findAvailable(LocalDate start, LocalDate end, String keyword, Integer producerId,
			Integer minSeats) {
		Set<Integer> booked = (start != null && end != null)
				? new HashSet<>(carRentalRepository.findBookedCarIds(start, end))
				: Set.of();
		String kw = keyword == null ? "" : keyword.trim().toLowerCase();
		List<Car> cars = carRepository.findAll().stream()
				.filter(c -> !Car.INACTIVE.equals(c.getStatus()))
				.filter(c -> !booked.contains(c.getCarId()))
				.filter(c -> kw.isEmpty() || c.getCarName().toLowerCase().contains(kw))
				.filter(c -> producerId == null || c.getProducer().getProducerId().equals(producerId))
				.filter(c -> minSeats == null || c.getCapacity() >= minSeats)
				.toList();
		return toDtos(cars);
	}

	@Override
	@Transactional(readOnly = true)
	public CarResponse get(Integer id) {
		return toDtos(List.of(find(id))).get(0);
	}

	@Override
	@Transactional(readOnly = true)
	public CarRequest getRequest(Integer id) {
		Car c = find(id);
		CarRequest r = new CarRequest();
		r.setCarId(c.getCarId());
		r.setCarName(c.getCarName());
		r.setCarModelYear(c.getCarModelYear());
		r.setColor(c.getColor());
		r.setCapacity(c.getCapacity());
		r.setDescription(c.getDescription());
		r.setImportDate(c.getImportDate());
		r.setProducerId(c.getProducer().getProducerId());
		r.setRentPrice(c.getRentPrice());
		r.setImageUrl(c.getImageUrl());
		r.setStatus(c.getStatus());
		return r;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CarProducerResponse> producers() {
		return producerRepository.findAll().stream().map(p -> {
			CarProducerResponse r = new CarProducerResponse();
			r.setProducerId(p.getProducerId());
			r.setProducerName(p.getProducerName());
			return r;
		}).toList();
	}

	@Override
	public CarResponse save(CarRequest request) {
		Car car = request.getCarId() == null ? new Car() : find(request.getCarId());
		car.setCarName(request.getCarName());
		car.setCarModelYear(request.getCarModelYear());
		car.setColor(request.getColor());
		car.setCapacity(request.getCapacity());
		car.setDescription(request.getDescription());
		car.setImportDate(request.getImportDate());
		car.setProducer(producerRepository.findById(request.getProducerId())
				.orElseThrow(() -> new IllegalArgumentException("Producer not found")));
		car.setRentPrice(request.getRentPrice());
		car.setImageUrl(request.getImageUrl() == null || request.getImageUrl().isBlank() ? null
				: request.getImageUrl().trim());
		car.setStatus(request.getStatus());
		return toDto(carRepository.save(car));
	}

	/**
	 * @return true nếu xóa hẳn; false nếu xe đã nằm trong giao dịch thuê nên chỉ đổi trạng thái sang Inactive.
	 */
	@Override
	public boolean delete(Integer id) {
		Car car = find(id);
		if (carRentalRepository.existsByCarCarId(id)) {
			car.setStatus(Car.INACTIVE);
			carRepository.save(car);
			return false;
		}
		carRepository.delete(car);
		return true;
	}

	private Car find(Integer id) {
		return carRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Car not found: " + id));
	}

	/** Chuyển sang DTO kèm điểm đánh giá trung bình (một query cho cả danh sách). */
	private List<CarResponse> toDtos(List<Car> cars) {
		Map<Integer, Object[]> ratings = new HashMap<>();
		for (Object[] row : reviewRepository.ratingByCar()) {
			ratings.put((Integer) row[0], row);
		}
		return cars.stream().map(c -> {
			CarResponse r = toDto(c);
			Object[] row = ratings.get(c.getCarId());
			if (row != null) {
				r.setAvgRating(((Number) row[1]).doubleValue());
				r.setReviewCount(((Number) row[2]).longValue());
			}
			return r;
		}).toList();
	}

	private CarResponse toDto(Car c) {
		CarResponse r = new CarResponse();
		r.setCarId(c.getCarId());
		r.setCarName(c.getCarName());
		r.setCarModelYear(c.getCarModelYear());
		r.setColor(c.getColor());
		r.setCapacity(c.getCapacity());
		r.setDescription(c.getDescription());
		r.setImportDate(c.getImportDate());
		r.setProducerId(c.getProducer().getProducerId());
		r.setProducerName(c.getProducer().getProducerName());
		r.setRentPrice(c.getRentPrice());
		r.setImageUrl(c.getImageUrl());
		r.setStatus(c.getStatus());
		return r;
	}
}
