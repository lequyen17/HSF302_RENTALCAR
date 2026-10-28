package com.HE194290.RENTALCAR.service.impl;

import com.HE194290.RENTALCAR.dto.request.CarRequest;
import com.HE194290.RENTALCAR.dto.response.CarProducerResponse;
import com.HE194290.RENTALCAR.dto.response.CarResponse;
import com.HE194290.RENTALCAR.entity.Car;
import com.HE194290.RENTALCAR.entity.CarProducer;
import com.HE194290.RENTALCAR.repository.CarProducerRepository;
import com.HE194290.RENTALCAR.repository.CarRentalRepository;
import com.HE194290.RENTALCAR.repository.CarRepository;
import com.HE194290.RENTALCAR.service.CarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CarServiceImpl implements CarService {

	private final CarRepository carRepository;
	private final CarProducerRepository producerRepository;
	private final CarRentalRepository carRentalRepository;

	public CarServiceImpl(CarRepository carRepository, CarProducerRepository producerRepository,
			CarRentalRepository carRentalRepository) {
		this.carRepository = carRepository;
		this.producerRepository = producerRepository;
		this.carRentalRepository = carRentalRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CarResponse> search(String keyword) {
		List<Car> cars = (keyword == null || keyword.isBlank())
				? carRepository.findAll()
				: carRepository.findByCarNameContainingIgnoreCase(keyword.trim());
		return cars.stream().map(this::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<CarResponse> findAvailable() {
		return carRepository.findByStatus(Car.AVAILABLE).stream().map(this::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CarResponse get(Integer id) {
		return toDto(find(id));
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
		r.setStatus(c.getStatus());
		return r;
	}
}
