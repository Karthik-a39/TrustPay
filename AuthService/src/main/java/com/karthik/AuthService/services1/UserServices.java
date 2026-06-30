package com.karthik.AuthService.services1;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.karthik.AuthService.Entities.AuthEntity;
import com.karthik.AuthService.repos.AuthRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServices implements UserDetailsService{

	private final AuthRepo repo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		AuthEntity user=repo.findByEmail(email);
		
		if(user==null) {
			throw new RuntimeException("User not found");
		}
		
		return new User(user.getEmail(),user.getPassword(),new ArrayList<>());
		
	}

}
