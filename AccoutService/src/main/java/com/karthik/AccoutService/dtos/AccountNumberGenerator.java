package com.karthik.AccoutService.dtos;

import java.util.Random;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
public class AccountNumberGenerator {

	public String getnumber() {
		Random ran=new Random();
		Long l=ran.nextLong(100000000);
		return "ACC"+l;
	}
}
