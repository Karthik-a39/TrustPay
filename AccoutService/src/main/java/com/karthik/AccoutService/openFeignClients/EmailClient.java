package com.karthik.AccoutService.openFeignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.karthik.AccoutService.dtos.UserResponse;
import com.karthik.AccoutService.dtos.VerifyPinRequest;

@FeignClient(name="AUTH-SERVICE")
public interface EmailClient {

	@GetMapping("/api/auth/email")
	public String getEmail(@RequestHeader("Authorization") String token);
	
	@GetMapping("/api/auth/{userId}")
	UserResponse getUseeByuserId(@PathVariable String userId);
	
	
	@GetMapping("/api/auth/by-email")
	UserResponse getByEmail(@RequestParam String email);
	
	@PostMapping("/api/auth/verify-pin")
	Boolean verifyPin(@RequestBody VerifyPinRequest req);
}
