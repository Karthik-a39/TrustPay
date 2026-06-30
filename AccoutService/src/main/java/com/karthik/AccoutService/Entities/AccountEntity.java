package com.karthik.AccoutService.Entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.karthik.AccoutService.enums.AccStatus;
import com.karthik.AccoutService.enums.AccType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="account_table")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String userId;
	
	private String accountNumber;
	
	private BigDecimal balance;
	
	
	
	@Enumerated(EnumType.STRING)   // store enum name as string in DB
	private AccType accountType;
	
	@Enumerated(EnumType.STRING)
	private AccStatus accountStatus;
	

	@CreationTimestamp
	private LocalDateTime createdAt;

	
}