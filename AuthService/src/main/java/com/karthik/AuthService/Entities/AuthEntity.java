package com.karthik.AuthService.Entities;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auth_table")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String userId;
	
	private String userName;
	
	private String email;
	
	private String password;
	
	private String transactionPin;
	
	
	@CreationTimestamp()
	@Column(updatable = false)
	private LocalDate createdAt;
	
	@UpdateTimestamp
	private LocalDate updatedAt;
}
