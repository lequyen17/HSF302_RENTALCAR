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

	@GetMapping("/login")
	public String loginForm(@RequestParam(required = false) String required, Model model) {
		if (required != null) {
			model.addAttribute("error", "Please log in to continue");
		}
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
		String next = (String) session.getAttribute("nextUrl");
		request.changeSessionId();
		session.setAttribute("accountId", a.getAccountId());
		session.setAttribute("role", a.getRole());
		session.setAttribute("displayName", a.getAccountName());
		session.removeAttribute("nextUrl");
		// Quay lại trang khách đang định vào (ví dụ bấm "Rent" khi chưa đăng nhập)
		if (Account.CUSTOMER.equals(a.getRole()) && next != null && next.startsWith("/customer")) {
			return "redirect:" + next;
		}
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
