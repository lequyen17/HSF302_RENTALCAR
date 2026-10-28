package com.HE194290.RENTALCAR.controller;

import com.HE194290.RENTALCAR.dto.request.CustomerRequest;
import com.HE194290.RENTALCAR.entity.Account;
import com.HE194290.RENTALCAR.service.AccountService;
import com.HE194290.RENTALCAR.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

	private final AccountService accountService;
	private final CustomerService customerService;

	public AuthController(AccountService accountService, CustomerService customerService) {
		this.accountService = accountService;
		this.customerService = customerService;
	}

	@GetMapping("/")
	public String home(HttpSession session) {
		String role = (String) session.getAttribute("role");
		if (Account.ADMIN.equals(role)) {
			return "redirect:/admin/cars";
		}
		if (Account.CUSTOMER.equals(role)) {
			return "redirect:/customer/rent";
		}
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}

	@PostMapping("/login")
	public String login(@RequestParam String email, @RequestParam String password, HttpServletRequest request,
			Model model) {
		var account = accountService.login(email, password);
		if (account.isEmpty()) {
			model.addAttribute("error", "Invalid email or password");
			model.addAttribute("email", email);
			return "login";
		}
		Account a = account.get();
		HttpSession session = request.getSession(true);
		request.changeSessionId();
		session.setAttribute("accountId", a.getAccountId());
		session.setAttribute("role", a.getRole());
		session.setAttribute("displayName", a.getAccountName());
		return "redirect:/";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("form", new CustomerRequest());
		return "register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("form") CustomerRequest form, BindingResult result,
			RedirectAttributes ra) {
		if (!result.hasErrors()) {
			try {
				customerService.create(form);
				ra.addFlashAttribute("message", "Registration successful. Please log in.");
				return "redirect:/login";
			} catch (IllegalArgumentException e) {
				result.reject("register.error", e.getMessage());
			}
		}
		return "register";
	}
}
