package com.karthik.AuthService.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

	private Long id;
	private String userId;
	private String email;
	private String userName;
	
}
