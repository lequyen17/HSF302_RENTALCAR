package com.HE194290.RENTALCAR.controller;

import com.HE194290.RENTALCAR.dto.request.CustomerRequest;
import com.HE194290.RENTALCAR.dto.response.CustomerResponse;
import com.HE194290.RENTALCAR.dto.response.ReviewResponse;
import com.HE194290.RENTALCAR.service.CarRentalService;
import com.HE194290.RENTALCAR.service.CustomerService;
import com.HE194290.RENTALCAR.service.ReviewService;
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

@Controller
@RequestMapping("/customer")
public class CustomerController {

	private final CustomerService customerService;
	private final CarRentalService rentalService;
	private final ReviewService reviewService;

	public CustomerController(CustomerService customerService,
			CarRentalService rentalService, ReviewService reviewService) {
		this.customerService = customerService;
		this.rentalService = rentalService;
		this.reviewService = reviewService;
	}

	private CustomerResponse current(HttpSession session) {
		return customerService.getByAccountId((Integer) session.getAttribute("accountId"));
	}

	/**
	 * Việc thuê xe thực hiện ngay trên trang chủ / trang chi tiết xe. Endpoint này chỉ còn để đưa khách về đúng chỗ,
	 * và để đăng nhập xong thì quay lại (interceptor ghi nhớ URL /customer/rent khi khách chưa đăng nhập).
	 */
	@GetMapping("/rent")
	public String rentRedirect(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
			@RequestParam(required = false) Integer carId, RedirectAttributes ra) {
		if (pickupDate != null && returnDate != null) {
			ra.addAttribute("pickupDate", pickupDate.toString());
			ra.addAttribute("returnDate", returnDate.toString());
		}
		return carId != null ? "redirect:/cars/" + carId : "redirect:/";
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
			ra.addAttribute("pickupDate", pickupDate.toString());
			ra.addAttribute("returnDate", returnDate.toString());
			return "redirect:/";
		}
	}

	@GetMapping("/history")
	public String history(HttpSession session, Model model) {
		Integer customerId = current(session).getCustomerId();
		model.addAttribute("rentals", rentalService.findByCustomer(customerId));
		model.addAttribute("reviews", reviewService.findByCustomerKeyedByRental(customerId));
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

	// ---- Reviews: create / read / update / delete ----

	@GetMapping("/reviews")
	public String myReviews(HttpSession session, Model model) {
		model.addAttribute("reviews", reviewService.findByCustomer(current(session).getCustomerId()));
		return "customer/reviews";
	}

	@GetMapping("/rentals/{id}/review")
	public String newReviewForm(@PathVariable Integer id, HttpSession session, Model model, RedirectAttributes ra) {
		try {
			String carName = reviewService.carNameOfRental(id, current(session).getCustomerId());
			return reviewForm(model, "Review " + carName, "/customer/rentals/" + id + "/review", 5, "");
		} catch (IllegalArgumentException | IllegalStateException e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/customer/history";
		}
	}

	@PostMapping("/rentals/{id}/review")
	public String createReview(@PathVariable Integer id, @RequestParam int reviewStar, @RequestParam String comment,
			HttpSession session, Model model, RedirectAttributes ra) {
		try {
			reviewService.create(id, current(session).getCustomerId(), reviewStar, comment);
			ra.addFlashAttribute("message", "Thank you for your review");
			return "redirect:/customer/reviews";
		} catch (IllegalArgumentException | IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
			return reviewForm(model, "Review", "/customer/rentals/" + id + "/review", reviewStar, comment);
		}
	}

	@GetMapping("/reviews/{id}/edit")
	public String editReviewForm(@PathVariable Integer id, HttpSession session, Model model, RedirectAttributes ra) {
		try {
			ReviewResponse r = reviewService.getOwned(id, current(session).getCustomerId());
			return reviewForm(model, "Edit review - " + r.getCarName(), "/customer/reviews/" + id + "/edit",
					r.getReviewStar(), r.getComment());
		} catch (IllegalArgumentException e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/customer/reviews";
		}
	}

	@PostMapping("/reviews/{id}/edit")
	public String updateReview(@PathVariable Integer id, @RequestParam int reviewStar, @RequestParam String comment,
			HttpSession session, Model model, RedirectAttributes ra) {
		try {
			reviewService.update(id, current(session).getCustomerId(), reviewStar, comment);
			ra.addFlashAttribute("message", "Review updated");
			return "redirect:/customer/reviews";
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			return reviewForm(model, "Edit review", "/customer/reviews/" + id + "/edit", reviewStar, comment);
		}
	}

	@PostMapping("/reviews/{id}/delete")
	public String deleteReview(@PathVariable Integer id, HttpSession session, RedirectAttributes ra) {
		try {
			reviewService.deleteByCustomer(id, current(session).getCustomerId());
			ra.addFlashAttribute("message", "Review deleted");
		} catch (IllegalArgumentException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/customer/reviews";
	}

	private String reviewForm(Model model, String heading, String action, int star, String comment) {
		model.addAttribute("heading", heading);
		model.addAttribute("formAction", action);
		model.addAttribute("star", star);
		model.addAttribute("comment", comment);
		return "customer/review-form";
	}

	// ---- Profile ----

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
