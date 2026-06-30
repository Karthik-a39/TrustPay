package com.karthik.AccoutService.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithDrawResponse {

	private long transactionId;
	private BigDecimal balanceAfter;
}
