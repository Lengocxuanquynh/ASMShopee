package com.fpoly.java5demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.java5demo.beans.RegisterBean;
import com.fpoly.java5demo.entities.User;
import com.fpoly.java5demo.jpas.UserJPA;
import com.fpoly.java5demo.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class UserServices {

	@Autowired
	private UserJPA userJPA;

	public String register(RegisterBean bean) {
		if (userJPA.existsByUsername(bean.getUsername())) {
			return "Tên tài khoản đã tồn tại!";
		}
		if (userJPA.existsByEmail(bean.getEmail())) {
			return "Email đã được sử dụng!";
		}

		User user = new User();
		user.setUsername(bean.getUsername());
		user.setPassword(bean.getPassword());
		user.setName(bean.getName());
		user.setEmail(bean.getEmail());
		user.setRole(0); // 0: User, 1: Admin
		user.setStatus(true);

		userJPA.save(user);
		return null;
	}

	public User login(String username, String password, boolean rememberMe, HttpServletResponse response, HttpSession session) {
		Optional<User> optionalUser = userJPA.findByUsernameAndPassword(username, password);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			if (!user.isStatus()) {
				return null; // Tài khoản bị khóa
			}

			// Lưu vào session
			session.setAttribute("user", user);
			session.setAttribute("role", user.getRole());

			// Lưu vào cookie nếu rememberMe
			if (rememberMe && response != null) {
				Utils.setCookie(Utils.COOKIE_KEY_USER_ID, String.valueOf(user.getId()), response);
				Utils.setCookie(Utils.COOKIE_KEY_ROLE, String.valueOf(user.getRole()), response);
			}
			return user;
		}
		return null;
	}

	public void logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		if (session != null) {
			session.removeAttribute("user");
			session.removeAttribute("role");
		}
		if (request != null && response != null) {
			Utils.clearCookie(request, response);
		}
	}

	public User getUserInfoById(int id) {
		return userJPA.findById(id).orElse(null);
	}

	public User getLoggedInUser(HttpServletRequest request, HttpSession session) {
		if (session != null && session.getAttribute("user") != null) {
			return (User) session.getAttribute("user");
		}
		if (request != null) {
			String userId = Utils.getCookieValue(Utils.COOKIE_KEY_USER_ID, request);
			if (userId != null) {
				try {
					User user = getUserInfoById(Integer.parseInt(userId));
					if (user != null && user.isStatus()) {
						if (session != null) {
							session.setAttribute("user", user);
							session.setAttribute("role", user.getRole());
						}
						return user;
					}
				} catch (Exception ignored) {
				}
			}
		}
		return null;
	}

	public List<User> getAllUsers() {
		return userJPA.findAll();
	}

	public User save(User user) {
		return userJPA.save(user);
	}
}
