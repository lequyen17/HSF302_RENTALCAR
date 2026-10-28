package com.HE194290.RENTALCAR.controller;

import com.HE194290.RENTALCAR.entity.CarRental;
import com.HE194290.RENTALCAR.service.CarRentalService;
import com.HE194290.RENTALCAR.service.CarService;
import com.HE194290.RENTALCAR.service.CustomerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	/** Chọn ngày trước, sau đó chỉ liệt kê xe còn trống trong khoảng ngày đó. */
	@GetMapping("/rentals/new")
	public String createForm(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
			Model model) {
		model.addAttribute("customers", customerService.findAll());
		model.addAttribute("pickupDate", pickupDate);
		model.addAttribute("returnDate", returnDate);
		if (pickupDate != null && returnDate != null) {
			if (returnDate.isAfter(pickupDate)) {
				model.addAttribute("cars", carService.findAvailable(pickupDate, returnDate, null, null, null));
			} else {
				model.addAttribute("error", "Return date must be after pickup date");
			}
		}
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
			ra.addAttribute("pickupDate", pickupDate.toString());
			ra.addAttribute("returnDate", returnDate.toString());
			return "redirect:/admin/rentals/new";
		}
	}

	@GetMapping("/rentals/edit/{id}")
	public String editForm(@PathVariable Integer id, Model model) {
		model.addAttribute("rental", rentalService.get(id));
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

	/**
	 * Dashboard/báo cáo - trang đầu tiên của Admin. Mặc định là tháng hiện tại; có thể chọn StartDate -> EndDate
	 * và lọc theo trạng thái. Giao dịch sắp xếp giảm dần theo ngày nhận xe.
	 */
	@GetMapping("/reports")
	public String report(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			Model model) {
		LocalDate today = LocalDate.now();
		LocalDate start = startDate != null ? startDate : today.withDayOfMonth(1);
		LocalDate end = endDate != null ? endDate : today.withDayOfMonth(today.lengthOfMonth());
		model.addAttribute("startDate", start);
		model.addAttribute("endDate", end);
		try {
			model.addAttribute("report", rentalService.report(start, end));
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("report", rentalService.report(today.withDayOfMonth(1),
					today.withDayOfMonth(today.lengthOfMonth())));
		}
		return "admin/report";
	}
}
