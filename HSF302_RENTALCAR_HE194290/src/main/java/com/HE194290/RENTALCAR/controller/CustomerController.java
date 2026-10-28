package com.HE194290.RENTALCAR.controller;

import com.HE194290.RENTALCAR.dto.request.CustomerRequest;
import com.HE194290.RENTALCAR.dto.response.CustomerResponse;
import com.HE194290.RENTALCAR.service.CarRentalService;
import com.HE194290.RENTALCAR.service.CarService;
import com.HE194290.RENTALCAR.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	private final CustomerService customerService;
	private final CarService carService;
	private final CarRentalService rentalService;

	public CustomerController(CustomerService customerService, CarService carService,
			CarRentalService rentalService) {
		this.customerService = customerService;
		this.carService = carService;
		this.rentalService = rentalService;
	}

	private CustomerResponse current(HttpSession session) {
		return customerService.getByAccountId((Integer) session.getAttribute("accountId"));
	}

	@GetMapping("/rent")
	public String rentForm(Model model) {
		model.addAttribute("cars", carService.findAvailable());
		return "customer/rent";
	}

	@PostMapping("/rent")
	public String rent(@RequestParam(required = false) List<Integer> carIds,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
			HttpSession session, RedirectAttributes ra) {
		try {
			rentalService.create(current(session).getCustomerId(), carIds, pickupDate, returnDate);
			ra.addFlashAttribute("message", "Your rental request has been created");
			return "redirect:/customer/history";
		} catch (IllegalArgumentException e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/customer/rent";
		}
	}

	@GetMapping("/history")
	public String history(HttpSession session, Model model) {
		var rentals = rentalService.findByCustomer(current(session).getCustomerId());
		Set<Integer> reviewed = rentals.stream().map(r -> r.getCarRentId()).filter(rentalService::isReviewed)
				.collect(Collectors.toSet());
		model.addAttribute("rentals", rentals);
		model.addAttribute("reviewed", reviewed);
		return "customer/history";
	}

	@PostMapping("/rentals/{id}/cancel")
	public String cancel(@PathVariable Integer id, HttpSession session, RedirectAttributes ra) {
		try {
			rentalService.cancelByCustomer(id, current(session).getCustomerId());
			ra.addFlashAttribute("message", "Transaction cancelled");
		} catch (IllegalArgumentException | IllegalStateException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/history";
	}

	@PostMapping("/rentals/{id}/review")
	public String review(@PathVariable Integer id, @RequestParam int reviewStar, @RequestParam String comment,
			HttpSession session, RedirectAttributes ra) {
		try {
			rentalService.addReview(id, current(session).getCustomerId(), reviewStar, comment);
			ra.addFlashAttribute("message", "Thank you for your review");
		} catch (IllegalArgumentException | IllegalStateException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/history";
	}

	@GetMapping("/profile")
	public String profile(HttpSession session, Model model) {
		model.addAttribute("form", customerService.getRequest(current(session).getCustomerId()));
		return "customer/profile";
	}

	@PostMapping("/profile")
	public String updateProfile(@Valid @ModelAttribute("form") CustomerRequest form, BindingResult result,
			HttpSession session, RedirectAttributes ra) {
		// customerId lấy từ session, không tin dữ liệu từ form
		form.setCustomerId(current(session).getCustomerId());
		if (!result.hasErrors()) {
			try {
				customerService.update(form);
				session.setAttribute("displayName", form.getAccountName());
				ra.addFlashAttribute("message", "Profile updated");
				return "redirect:/customer/profile";
			} catch (IllegalArgumentException e) {
				result.reject("profile.error", e.getMessage());
			}
		}
		return "customer/profile";
	}
}
