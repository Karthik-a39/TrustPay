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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRequestDto {

    private  Long id;
	private AccType accountType;
	private AccStatus  accountStatus;
	private LocalDateTime createdAt;
	
}
