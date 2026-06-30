package com.karthik.AuthService.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
 
	@NotNull(message = "enter valid email")
	private String email;
	@NotNull(message = "enter valid password")
	private String password;
}
