package com.karthik.AuthService.Controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.karthik.AuthService.Dto.LoginRequest;
import com.karthik.AuthService.Dto.PinRequest;
import com.karthik.AuthService.Dto.RegisterRequest;
import com.karthik.AuthService.Dto.UserResponse;
import com.karthik.AuthService.services1.AuthServices;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthCotroller {

	private final AuthServices service;
	
	
	@PostMapping("/register")
	public ResponseEntity<RegisterRequest> register(@Valid   @RequestBody RegisterRequest req) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.getRegister(req));
	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String,String> > login(@Valid   @RequestBody LoginRequest req,HttpServletResponse response) {
		return ResponseEntity.ok(service.getLogin(req,response));
	}
	
	 @PostMapping("/logout")
	    public ResponseEntity<String> logout(HttpServletResponse response) {
	        service.logout(response);
	        return ResponseEntity.ok("Logged out successfully");
	    }
	
	@GetMapping("/msg")
	public String getWork() {
		return "Security Working ☑️";
	}
	
	@GetMapping("/email")
	public String getCurrentEmail(@RequestHeader("Authorization") String token) {
		return service.getCurrentUserEmail(token);
	}
	
	 @GetMapping("/validate")
	    public ResponseEntity<String> validate(HttpServletRequest request) {
	        boolean valid = service.validateToken(request);
	        return valid ? ResponseEntity.ok("Token is valid")
	                     : ResponseEntity.status(401).body("Invalid token");
	    }
	 
	 @GetMapping("/{userId}")
	 public UserResponse getUserById(@PathVariable String userId) {
		 return service.getById(userId);
	 }
	 
	 @GetMapping("/by-email")
	 public UserResponse getByEmail(@RequestParam   String email ) {
		 return service.getByEmail(email);
	 }
	 
	 @GetMapping("/set-pin")
	 public String setPin(@RequestHeader("X-User-Id") String userId, @RequestBody PinRequest req) {
		 return service.setPinforAccount(userId,req);
	 }
	
	 
	 @PostMapping("/verify-pin")
	 public Boolean verifyPin(@RequestBody PinRequest req) {
		 return service.verifyPin(req);
	 }
}
