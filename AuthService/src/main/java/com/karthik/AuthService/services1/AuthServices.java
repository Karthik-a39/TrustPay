package com.karthik.AuthService.services1;



import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.karthik.AuthService.Dto.VerifyPinRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.karthik.AuthService.Entities.AuthEntity;
import com.karthik.AuthService.repos.AuthRepo;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.karthik.AuthService.Dto.*;

@Service
@RequiredArgsConstructor
public class AuthServices {

	private final AuthRepo authRepo;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager am;
	private final JwtService jwtService;
	
	
	public RegisterRequest getRegister(RegisterRequest req) {
		AuthEntity user= toEntity(req);
		user.setUserId(UUID.randomUUID().toString());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		authRepo.save(user);
		
		return toDto(user);
	}
	
	
	public Map<String,String>  getLogin(LoginRequest req,HttpServletResponse response) {
		Map<String,String> loginDeatils=new HashMap<>();
		
		AuthEntity user=authRepo.findByEmail(req.getEmail());
		
	    Authentication auth=am.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(),req.getPassword()));
	    if(auth==null) {
	    	throw new RuntimeException("User is Not Registered");
	    }
	    String token=jwtService.generateToken(req.getEmail(),user.getUserId());
	    
	    loginDeatils.put("email", req.getEmail());
	    loginDeatils.put("msg", "Login Successfully!");
	    loginDeatils.put("token",token);
	    
	    Cookie cookie = new Cookie("token",token );
	    cookie.setHttpOnly(true);   // prevent JavaScript access
	    cookie.setSecure(true);     // only send over HTTPS
	    cookie.setPath("/");        // available to whole app
	    cookie.setMaxAge(60 * 60);
	    response.addCookie(cookie);

        // Optional settings
        
	    return loginDeatils;
	    
	}
	
	
	public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // expire immediately
        response.addCookie(cookie);
    }
	public boolean validateToken(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("token".equals(c.getName())) {
                    token = c.getValue();
                }
            }
        }
        if (token == null) return false;

        String username = jwtService.extractUsername(token);
        return jwtService.validateToken(token, username);
    }
	
	
	
	 public static String getCurrentUserEmail(String token) {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	        if (authentication == null || !authentication.isAuthenticated()) {
	            return null; // no user logged in
	        }

	        Object principal = authentication.getPrincipal();

	        if (principal instanceof UserDetails) {
	            return ((UserDetails) principal).getUsername(); // usually email/username
	        } else {
	            return principal.toString();
	        }
	    }

	 



	private RegisterRequest toDto(AuthEntity user) {
		// TODO Auto-generated method stub
		return RegisterRequest.builder()
				.id(user.getId())
				.userName(user.getUserName())
				.email(user.getEmail())
				.userId(user.getUserId())
				.createdAt(user.getCreatedAt())
				.build();
				
	}


	private AuthEntity toEntity(RegisterRequest req) {
		// TODO Auto-generated method stub
		return AuthEntity.builder()
				.id(req.getId())
				.userName(req.getUserName())
				.password(req.getPassword())
				.email(req.getEmail())
				.userId(req.getUserId())
				.createdAt(req.getCreatedAt())
				 .build();
		
	}


	public UserResponse getById(String id) {
		AuthEntity user=authRepo.findByUserId(id);
		
		if(user==null) {
			throw new RuntimeException("User with Id: "+id+" NOT FOUND");
		}
		
		UserResponse userRes=toRes(user);
		return userRes;
	}
	
	private UserResponse toRes(AuthEntity user) {
		return UserResponse.builder()
				.id(user.getId())
				.email(user.getEmail())
				.userId(user.getUserId())
				.build();
	}


	public UserResponse getByEmail(String email) {
		AuthEntity auth=authRepo.findByEmail(email);
		
		return UserResponse.builder()
				.userId(auth.getUserId())
				.email(auth.getEmail())
				.userName(auth.getUserName())
				.id(auth.getId())
				.build();
		
	}


	public String setPinforAccount(String userId, PinRequest pin) {
		AuthEntity auth=authRepo.findByUserId(userId);
		if(auth==null) {
			throw new RuntimeException("User Not Found!");
		}
		auth.setTransactionPin(passwordEncoder.encode(pin.getPin()));
		authRepo.save(auth);
		return "Transaction Pin Set Successfully";
	}


	public Boolean verifyPin(VerifyPinRequest req) {
	    AuthEntity auth = authRepo.findById(req.getId()).orElse(null);
	    if (auth == null) {
	        throw new RuntimeException("User Not Found!");
	    }

	    return passwordEncoder.matches(req.getPin(), auth.getTransactionPin());
	}
}
