package com.HE194290.RENTALCAR.controller;

import com.HE194290.RENTALCAR.dto.response.CarRentalResponse;
import com.HE194290.RENTALCAR.entity.CarRental;
import com.HE194290.RENTALCAR.service.CarRentalService;
import com.HE194290.RENTALCAR.service.CarService;
import com.HE194290.RENTALCAR.service.CustomerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminRentalController {

	private final CarRentalService rentalService;
	private final CarService carService;
	private final CustomerService customerService;

	public AdminRentalController(CarRentalService rentalService, CarService carService,
			CustomerService customerService) {
		this.rentalService = rentalService;
		this.carService = carService;
		this.customerService = customerService;
	}

	@GetMapping("/rentals")
	public String list(Model model) {
		model.addAttribute("rentals", rentalService.findAll());
		return "admin/rental-list";
	}

	@GetMapping("/rentals/new")
	public String createForm(Model model) {
		model.addAttribute("customers", customerService.search(null));
		model.addAttribute("cars", carService.findAvailable());
		return "admin/rental-new";
	}

	@PostMapping("/rentals/save")
	public String create(@RequestParam Integer customerId, @RequestParam(required = false) List<Integer> carIds,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
			RedirectAttributes ra) {
		try {
			rentalService.create(customerId, carIds, pickupDate, returnDate);
			ra.addFlashAttribute("message", "Rental transaction created");
			return "redirect:/admin/rentals";
		} catch (IllegalArgumentException e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/admin/rentals/new";
		}
	}

	@GetMapping("/rentals/edit/{id}")
	public String editForm(@PathVariable Integer id, Model model) {
		model.addAttribute("rental", rentalService.get(id));
		model.addAttribute("statuses", List.of(CarRental.ACTIVE, CarRental.COMPLETED, CarRental.CANCELLED));
		return "admin/rental-edit";
	}

	@PostMapping("/rentals/update/{id}")
	public String update(@PathVariable Integer id,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
			@RequestParam String status, RedirectAttributes ra) {
		try {
			rentalService.update(id, pickupDate, returnDate, status);
			ra.addFlashAttribute("message", "Rental transaction updated");
			return "redirect:/admin/rentals";
		} catch (IllegalArgumentException e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/admin/rentals/edit/" + id;
		}
	}

	@PostMapping("/rentals/delete/{id}")
	public String delete(@PathVariable Integer id, RedirectAttributes ra) {
		rentalService.delete(id);
		ra.addFlashAttribute("message", "Rental transaction deleted");
		return "redirect:/admin/rentals";
	}

	/** Báo cáo thống kê giao dịch từ StartDate đến EndDate, sắp xếp giảm dần theo ngày nhận xe. */
	@GetMapping("/reports")
	public String report(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			Model model) {
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		if (startDate != null && endDate != null) {
			try {
				List<CarRentalResponse> rentals = rentalService.report(startDate, endDate);
				BigDecimal total = rentals.stream()
						.filter(r -> !CarRental.CANCELLED.equals(r.getStatus()))
						.map(CarRentalResponse::getRentPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
				model.addAttribute("rentals", rentals);
				model.addAttribute("total", total);
			} catch (IllegalArgumentException e) {
				model.addAttribute("error", e.getMessage());
			}
		}
		return "admin/report";
	}
}
