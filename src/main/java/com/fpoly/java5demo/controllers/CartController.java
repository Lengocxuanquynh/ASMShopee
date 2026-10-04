package com.fpoly.java5demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.java5demo.entities.User;
import com.fpoly.java5demo.services.CartDetailServices;
import com.fpoly.java5demo.services.UserServices;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private CartDetailServices cartDetailServices;

	@Autowired
	private UserServices userServices;

	@GetMapping("")
	public String viewCart(Model model, HttpServletRequest request, HttpSession session) {
		User currentUser = userServices.getLoggedInUser(request, session);
		model.addAttribute("currentUser", currentUser);
		model.addAttribute("cartItems", cartDetailServices.getCartItems());
		model.addAttribute("totalAmount", cartDetailServices.getTotalAmount());
		model.addAttribute("cartCount", cartDetailServices.getTotalQuantity());
		return "cart";
	}

	@PostMapping("/add")
	public String addToCart(@RequestParam("productId") int productId,
			@RequestParam(name = "quantity", defaultValue = "1") int quantity,
			@RequestParam(name = "buyNow", defaultValue = "false") boolean buyNow) {
		cartDetailServices.addToCart(productId, quantity);
		if (buyNow) {
			return "redirect:/checkout";
		}
		return "redirect:/cart";
	}

	@GetMapping("/add/{id}")
	public String addToCartQuick(@PathVariable("id") int productId) {
		cartDetailServices.addToCart(productId, 1);
		return "redirect:/cart";
	}

	@PostMapping("/update")
	public String updateQuantity(@RequestParam("productId") int productId,
			@RequestParam("quantity") int quantity) {
		cartDetailServices.updateQuantity(productId, quantity);
		return "redirect:/cart";
	}

	@GetMapping("/remove/{id}")
	public String removeFromCart(@PathVariable("id") int productId) {
		cartDetailServices.removeCart(productId);
		return "redirect:/cart";
	}

	@GetMapping("/clear")
	public String clearCart() {
		cartDetailServices.clearCart();
		return "redirect:/cart";
	}
}
