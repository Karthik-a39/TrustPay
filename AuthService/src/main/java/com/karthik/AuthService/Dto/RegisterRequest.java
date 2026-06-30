package com.karthik.AuthService.Dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
	
	private Long id;
	
	private String userId;

	@NotNull(message = "enter valid userName")
	private String userName;
	@NotNull(message = "enter valid email")
	private String email;
	@NotNull(message = "enter valid password")
	private String password;
	
	private LocalDate createdAt;
	
}
