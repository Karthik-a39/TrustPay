package com.karthik.AccoutService.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.karthik.AccoutService.enums.AccStatus;
import com.karthik.AccoutService.enums.AccType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreationResponse {

	private Long id;
	private String userId;
	private BigDecimal balance;
	private String accountNumber;
	private LocalDateTime createdAt;
	private AccType accountType;
	private AccStatus accountStatus;
	
}
