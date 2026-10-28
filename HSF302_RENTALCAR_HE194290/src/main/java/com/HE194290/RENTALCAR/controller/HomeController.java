package com.HE194290.RENTALCAR.controller;

import com.HE194290.RENTALCAR.dto.response.CarResponse;
import com.HE194290.RENTALCAR.entity.Account;
import com.HE194290.RENTALCAR.service.CarRentalService;
import com.HE194290.RENTALCAR.service.CarService;
import com.HE194290.RENTALCAR.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Trang công khai: danh sách xe (lọc theo ngày thuê) và chi tiết xe. */
@Controller
public class HomeController {

	private final CarService carService;
	private final CarRentalService rentalService;
	private final ReviewService reviewService;

	public HomeController(CarService carService, CarRentalService rentalService,
			ReviewService reviewService) {
		this.carService = carService;
		this.rentalService = rentalService;
		this.reviewService = reviewService;
	}

	@GetMapping("/")
	public String home(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
			@RequestParam(required = false) String keyword, HttpSession session, Model model) {
		if (Account.ADMIN.equals(session.getAttribute("role"))) {
			return "redirect:/admin/reports";
		}
		boolean byDate = pickupDate != null && returnDate != null;
		if (byDate && !returnDate.isAfter(pickupDate)) {
			model.addAttribute("error", "Return date must be after pickup date");
			byDate = false;
		}
		model.addAttribute("cars", carService.findAvailable(byDate ? pickupDate : null, byDate ? returnDate : null,
				keyword, null, null));
		model.addAttribute("byDate", byDate);
		model.addAttribute("days", byDate ? ChronoUnit.DAYS.between(pickupDate, returnDate) : 0L);
		model.addAttribute("pickupDate", pickupDate);
		model.addAttribute("returnDate", returnDate);
		model.addAttribute("keyword", keyword);
		return "home";
	}

	@GetMapping("/cars/{id}")
	public String detail(@PathVariable Integer id,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
			Model model, RedirectAttributes ra) {
		CarResponse car;
		try {
			car = carService.get(id);
		} catch (IllegalArgumentException e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/";
		}
		model.addAttribute("car", car);
		model.addAttribute("reviews", reviewService.findByCar(id));
		model.addAttribute("pickupDate", pickupDate);
		model.addAttribute("returnDate", returnDate);
		model.addAttribute("checked", false);
		model.addAttribute("available", false);
		if (pickupDate != null && returnDate != null) {
			if (returnDate.isAfter(pickupDate)) {
				long days = ChronoUnit.DAYS.between(pickupDate, returnDate);
				model.addAttribute("checked", true);
				model.addAttribute("available", rentalService.isCarAvailable(id, pickupDate, returnDate));
				model.addAttribute("days", days);
				model.addAttribute("total", car.getRentPrice().multiply(BigDecimal.valueOf(days)));
			} else {
				model.addAttribute("error", "Return date must be after pickup date");
			}
		}
		return "car-detail";
	}
}
