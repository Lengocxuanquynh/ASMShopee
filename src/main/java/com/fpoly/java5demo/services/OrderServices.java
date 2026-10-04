package com.fpoly.java5demo.services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fpoly.java5demo.beans.CheckoutBean;
import com.fpoly.java5demo.entities.Order;
import com.fpoly.java5demo.entities.OrderDetail;
import com.fpoly.java5demo.entities.Product;
import com.fpoly.java5demo.entities.User;
import com.fpoly.java5demo.jpas.OrderDetailJPA;
import com.fpoly.java5demo.jpas.OrderJPA;
import com.fpoly.java5demo.jpas.ProductJPA;
import com.fpoly.java5demo.jpas.UserJPA;

@Service
public class OrderServices {

	@Autowired
	private OrderJPA orderJPA;

	@Autowired
	private OrderDetailJPA orderDetailJPA;

	@Autowired
	private UserJPA userJPA;

	@Autowired
	private ProductJPA productJPA;

	@Autowired
	private CartDetailServices cartDetailServices;

	@Transactional
	public Order createOrder(User user, CheckoutBean checkoutBean) {
		Map<Integer, Integer> cartItems = cartDetailServices.getCartMapSession();
		if (cartItems.isEmpty()) {
			return null;
		}

		List<Integer> ids = new ArrayList<>(cartItems.keySet());
		List<Product> products = productJPA.findAllById(ids);

		int totalPrice = 0;
		for (Product item : products) {
			int buyQty = cartItems.getOrDefault(item.getId(), 0);
			if (item.getQuantity() < buyQty || buyQty <= 0) {
				return null; // Không đủ số lượng
			}
			totalPrice += buyQty * item.getPrice();
		}

		// Tạo đơn hàng
		Order order = new Order();
		String fullAddress = checkoutBean.getAddress() + " (Người nhận: " + checkoutBean.getFullName() + " - SĐT: " + checkoutBean.getPhone() + ")";
		if (checkoutBean.getNote() != null && !checkoutBean.getNote().trim().isEmpty()) {
			fullAddress += " [Ghi chú: " + checkoutBean.getNote().trim() + "]";
		}
		if (fullAddress.length() > 200) {
			fullAddress = fullAddress.substring(0, 200);
		}
		order.setAddress(fullAddress);
		order.setUser(user);

		Calendar calendar = Calendar.getInstance();
		String dateStr = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
		order.setDate(Date.valueOf(dateStr));
		order.setStatus(0); // 0: Chờ xác nhận
		order.setTotalPrice(totalPrice);

		Order savedOrder = orderJPA.save(order);

		// Lưu chi tiết đơn hàng và trừ kho
		for (Product item : products) {
			int buyQty = cartItems.get(item.getId());
			OrderDetail detail = new OrderDetail();
			detail.setOrder(savedOrder);
			detail.setProduct(item);
			detail.setPrice(item.getPrice());
			detail.setQuantity(buyQty);
			orderDetailJPA.save(detail);

			// Trừ số lượng tồn kho
			item.setQuantity(item.getQuantity() - buyQty);
			productJPA.save(item);
		}

		// Xóa giỏ hàng
		cartDetailServices.clearCart();

		return savedOrder;
	}

	public List<Order> getOrdersByUser(int userId) {
		return orderJPA.findByUserIdOrderByDateDesc(userId);
	}

	public List<Order> getAllOrders() {
		return orderJPA.findAllByOrderByDateDesc();
	}

	public Order getOrderById(int orderId) {
		return orderJPA.findById(orderId).orElse(null);
	}

	public List<OrderDetail> getOrderDetails(int orderId) {
		return orderDetailJPA.findByOrderId(orderId);
	}

	public boolean updateStatus(int orderId, int status) {
		Order order = getOrderById(orderId);
		if (order != null) {
			order.setStatus(status);
			orderJPA.save(order);
			return true;
		}
		return false;
	}

	@Transactional
	public boolean cancelOrder(int orderId, int userId) {
		Order order = getOrderById(orderId);
		if (order == null || order.getUser().getId() != userId) {
			return false;
		}
		// Chỉ cho hủy khi đơn hàng ở trạng thái 0 (Chờ xác nhận)
		if (order.getStatus() == 0) {
			order.setStatus(5); // 5: Đã hủy
			orderJPA.save(order);

			// Hoàn trả số lượng sản phẩm vào kho
			List<OrderDetail> details = orderDetailJPA.findByOrderId(orderId);
			for (OrderDetail detail : details) {
				Product p = detail.getProduct();
				if (p != null) {
					p.setQuantity(p.getQuantity() + detail.getQuantity());
					productJPA.save(p);
				}
			}
			return true;
		}
		return false;
	}
}
