package com.HE194290.RENTALCAR.config;

import com.HE194290.RENTALCAR.dto.request.CustomerRequest;
import com.HE194290.RENTALCAR.entity.Account;
import com.HE194290.RENTALCAR.entity.Car;
import com.HE194290.RENTALCAR.entity.CarProducer;
import com.HE194290.RENTALCAR.repository.AccountRepository;
import com.HE194290.RENTALCAR.repository.CarProducerRepository;
import com.HE194290.RENTALCAR.repository.CarRepository;
import com.HE194290.RENTALCAR.service.AccountService;
import com.HE194290.RENTALCAR.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Tạo dữ liệu mẫu ở lần chạy đầu tiên (khi bảng còn trống). */
@Component
public class DataSeeder implements CommandLineRunner {

	private final AccountRepository accountRepository;
	private final AccountService accountService;
	private final CarProducerRepository producerRepository;
	private final CarRepository carRepository;
	private final CustomerService customerService;

	public DataSeeder(AccountRepository accountRepository, AccountService accountService,
			CarProducerRepository producerRepository, CarRepository carRepository, CustomerService customerService) {
		this.accountRepository = accountRepository;
		this.accountService = accountService;
		this.producerRepository = producerRepository;
		this.carRepository = carRepository;
		this.customerService = customerService;
	}

	@Override
	public void run(String... args) {
		if (!accountRepository.existsByEmail("admin@fucar.com")) {
			Account admin = new Account();
			admin.setAccountName("Administrator");
			admin.setEmail("admin@fucar.com");
			admin.setPassword(accountService.encode("admin123"));
			admin.setRole(Account.ADMIN);
			accountRepository.save(admin);
		}

		if (producerRepository.count() == 0) {
			List<CarProducer> producers = producerRepository.saveAll(List.of(
					new CarProducer("Toyota", "1 Toyota-cho, Toyota City", "Japan"),
					new CarProducer("Honda", "2-1-1 Minami-Aoyama, Tokyo", "Japan"),
					new CarProducer("VinFast", "Hai Phong City", "Vietnam"),
					new CarProducer("Ford", "One American Road, Dearborn", "USA")));
			carRepository.saveAll(List.of(
					car("Toyota Vios", 2023, "White", 5, "Compact sedan, fuel efficient", producers.get(0), "600000"),
					car("Toyota Fortuner", 2022, "Black", 7, "7-seat SUV for family trips", producers.get(0), "1200000"),
					car("Honda City", 2023, "Silver", 5, "Comfortable city sedan", producers.get(1), "650000"),
					car("VinFast VF8", 2024, "Blue", 5, "Electric SUV", producers.get(2), "1500000"),
					car("Ford Ranger", 2022, "Orange", 5, "Pickup truck", producers.get(3), "1000000")));
		}

		if (!accountRepository.existsByEmail("customer@fucar.com")) {
			CustomerRequest f = new CustomerRequest();
			f.setFullName("Nguyen Van A");
			f.setMobile("0901234567");
			f.setBirthday(LocalDate.of(2000, 1, 15));
			f.setIdentityCard("012345678901");
			f.setLicenceNumber("B2-123456");
			f.setLicenceDate(LocalDate.of(2020, 6, 1));
			f.setAccountName("customer");
			f.setEmail("customer@fucar.com");
			f.setPassword("customer123");
			customerService.create(f);
		}
	}

	private Car car(String name, int year, String color, int capacity, String desc, CarProducer producer,
			String price) {
		Car c = new Car();
		c.setCarName(name);
		c.setCarModelYear(year);
		c.setColor(color);
		c.setCapacity(capacity);
		c.setDescription(desc);
		c.setImportDate(LocalDate.now().minusMonths(6));
		c.setProducer(producer);
		c.setRentPrice(new BigDecimal(price));
		c.setStatus(Car.AVAILABLE);
		return c;
	}
}
