package com.karthik.AuthService.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.karthik.AuthService.Entities.AuthEntity;

public interface AuthRepo extends JpaRepository<AuthEntity, Long> {

	AuthEntity findByEmail(String email);
	
	AuthEntity findByUserId(String userId);

}
