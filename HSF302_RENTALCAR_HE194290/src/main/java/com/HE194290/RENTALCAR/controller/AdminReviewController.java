package com.HE194290.RENTALCAR.controller;

import com.HE194290.RENTALCAR.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {

	private final ReviewService reviewService;

	public AdminReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("reviews", reviewService.findAll());
		return "admin/review-list";
	}

	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Integer id, RedirectAttributes ra) {
		try {
			reviewService.deleteByAdmin(id);
			ra.addFlashAttribute("message", "Review deleted");
		} catch (IllegalArgumentException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/reviews";
	}
}
