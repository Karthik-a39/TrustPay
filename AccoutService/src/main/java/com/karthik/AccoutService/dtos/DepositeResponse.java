package com.karthik.AccoutService.dtos;

import java.math.BigDecimal;

import com.karthik.AccoutService.Entities.AccountEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositeResponse {

	private Long transactionId;
	private BigDecimal balanceAfter;
	
}
