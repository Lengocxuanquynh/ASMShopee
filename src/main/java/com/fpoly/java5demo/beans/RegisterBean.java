package com.fpoly.java5demo.beans;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterBean {
	@NotBlank(message = "Username is required")
	private String username;

	@NotBlank(message = "Password is required")
	@Length(min = 6, message = "Password must be at least 6 characters long")
	private String password;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Name is required")
	private String name;
}
