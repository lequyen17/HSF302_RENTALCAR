package com.HE194290.RENTALCAR.controller;

import com.HE194290.RENTALCAR.dto.request.CustomerRequest;
import com.HE194290.RENTALCAR.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/customers")
public class AdminCustomerController {

	private final CustomerService customerService;

	public AdminCustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@GetMapping
	public String list(@RequestParam(required = false) String keyword, Model model) {
		model.addAttribute("customers", customerService.search(keyword));
		model.addAttribute("keyword", keyword);
		return "admin/customer-list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("form", new CustomerRequest());
		return "admin/customer-form";
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Integer id, Model model) {
		model.addAttribute("form", customerService.getRequest(id));
		return "admin/customer-form";
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("form") CustomerRequest form, BindingResult result,
			RedirectAttributes ra) {
		if (!result.hasErrors()) {
			try {
				if (form.getCustomerId() == null) {
					customerService.create(form);
					ra.addFlashAttribute("message", "Customer created");
				} else {
					customerService.update(form);
					ra.addFlashAttribute("message", "Customer updated");
				}
				return "redirect:/admin/customers";
			} catch (IllegalArgumentException e) {
				result.reject("customer.error", e.getMessage());
			}
		}
		return "admin/customer-form";
	}

	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Integer id, RedirectAttributes ra) {
		try {
			customerService.delete(id);
			ra.addFlashAttribute("message", "Customer deleted");
		} catch (IllegalStateException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/customers";
	}
}
