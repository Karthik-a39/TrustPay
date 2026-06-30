package com.karthik.AuthService.configs;

import org.hibernate.annotations.Audited;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.karthik.AuthService.services1.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	
	private  final UserDetailsService userdetailsService ;
	private final JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain getChain(HttpSecurity sec) {
		
		return sec.csrf(customizer->customizer.disable())
				  .authorizeHttpRequests(auth->auth
						  .requestMatchers("/api/auth/register",
								  "/api/auth/login",
								  "/api/auth/logout",
								  "/api/auth/validate",
								  "/api/auth/**",
								  "/api/auth/by-email",
								  "/api/auth/set-pin",
								  "/api/auth/verify-pin",
                                  "/actuator/health").permitAll()
						  .anyRequest().authenticated() 
						  )
				  .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				  .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				  .build();
	}
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider getAuth() {
		DaoAuthenticationProvider dao=new DaoAuthenticationProvider(userdetailsService);
		dao.setPasswordEncoder(encoder());
		return dao;
	}
	
	@Bean
	public AuthenticationManager getManage(AuthenticationConfiguration config) {
		return config.getAuthenticationManager();
	}
}
