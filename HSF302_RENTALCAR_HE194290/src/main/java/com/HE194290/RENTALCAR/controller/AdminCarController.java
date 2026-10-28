package com.HE194290.RENTALCAR.controller;

import com.HE194290.RENTALCAR.dto.request.CarRequest;
import com.HE194290.RENTALCAR.entity.Car;
import com.HE194290.RENTALCAR.service.CarService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/cars")
public class AdminCarController {

	private static final List<String> STATUSES = List.of(Car.AVAILABLE, Car.RENTED, Car.INACTIVE);

	private final CarService carService;

	public AdminCarController(CarService carService) {
		this.carService = carService;
	}

	@GetMapping
	public String list(@RequestParam(required = false) String keyword, Model model) {
		model.addAttribute("cars", carService.search(keyword));
		model.addAttribute("keyword", keyword);
		return "admin/car-list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		CarRequest request = new CarRequest();
		request.setStatus(Car.AVAILABLE);
		return form(request, model);
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Integer id, Model model) {
		return form(carService.getRequest(id), model);
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("car") CarRequest request, BindingResult result, Model model,
			RedirectAttributes ra) {
		if (result.hasErrors()) {
			return form(request, model);
		}
		boolean isNew = request.getCarId() == null;
		carService.save(request);
		ra.addFlashAttribute("message", isNew ? "Car created" : "Car updated");
		return "redirect:/admin/cars";
	}

	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Integer id, RedirectAttributes ra) {
		if (carService.delete(id)) {
			ra.addFlashAttribute("message", "Car deleted");
		} else {
			ra.addFlashAttribute("message",
					"Car is used by rental transactions, so its status was changed to Inactive instead of deleting");
		}
		return "redirect:/admin/cars";
	}

	private String form(CarRequest request, Model model) {
		model.addAttribute("car", request);
		model.addAttribute("producers", carService.producers());
		model.addAttribute("statuses", STATUSES);
		return "admin/car-form";
	}
}
