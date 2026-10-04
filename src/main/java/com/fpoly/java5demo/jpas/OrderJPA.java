package com.fpoly.java5demo.jpas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.java5demo.entities.Order;

public interface OrderJPA extends JpaRepository<Order, Integer> {
	List<Order> findByUserIdOrderByDateDesc(int userId);

	List<Order> findAllByOrderByDateDesc();

	List<Order> findByStatusOrderByDateDesc(int status);

	List<Order> findByUserIdAndStatusOrderByDateDesc(int userId, int status);
}

