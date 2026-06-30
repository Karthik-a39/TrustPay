package com.karthik.AccoutService.dtos;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class TransactionIdGenerator {

	
	public Long getnumber() {
		Random ran=new Random();
		Long l=ran.nextLong(100000000);
		return l;
	}
}
