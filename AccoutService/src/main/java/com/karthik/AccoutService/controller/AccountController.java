package com.karthik.AccoutService.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.karthik.AccoutService.services.AccountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.karthik.AccoutService.dtos.AccountCreationResponse;
import com.karthik.AccoutService.dtos.AccountRequestDto;
import com.karthik.AccoutService.dtos.DepositeRequest;
import com.karthik.AccoutService.dtos.DepositeResponse;
import com.karthik.AccoutService.dtos.TransHistory;
import com.karthik.AccoutService.dtos.TransferRequest;
import com.karthik.AccoutService.dtos.TransferResponse;
import com.karthik.AccoutService.dtos.TransferViaEmailRequest;
import com.karthik.AccoutService.dtos.WithDrawResponse;
import com.karthik.AccoutService.dtos.WithdrawRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    @Autowired
	private AccountServices accService;
	
	@PostMapping("/create")
	public ResponseEntity<AccountCreationResponse> createAccount( @RequestHeader("Authorization") String token,  @RequestHeader("X-User-Id") String userId,@RequestBody AccountRequestDto req)
	{
		
		
		return ResponseEntity.status(HttpStatus.CREATED).body(accService.createAccount(token,userId, req));		
	}
	
	@GetMapping
	public String getmsg() {
		return "Account Service";
	}
	
	@GetMapping("/my-accounts/{userId}")
	public ResponseEntity<AccountCreationResponse> getByUserId(@PathVariable String userId){
		return ResponseEntity.status(HttpStatus.OK).body(accService.getByUserId(userId));
	}
	
	@GetMapping("/my-accounts/{accId}/balance")
	public ResponseEntity<Map<String,BigDecimal>> getBalanceByAccId(@PathVariable String accId){
		return ResponseEntity.status(HttpStatus.OK).body(accService.getBalanceByAccId(accId));
	}
	
	@PostMapping("/my-accounts/deposit/{accNumber}")
	public ResponseEntity<DepositeResponse> depositAmount(@PathVariable String accNumber,@RequestBody  DepositeRequest req){
		return ResponseEntity.status(HttpStatus.OK).body(accService.depositAmmount(accNumber, req));
	}
	@PostMapping("/my-accounts/withdraw/{accNumber}")
	public ResponseEntity<WithDrawResponse> withdrawAmount(@PathVariable String accNumber,@Valid  @RequestBody  WithdrawRequest req){
		return ResponseEntity.status(HttpStatus.OK).body(accService.withDrawAmount(accNumber, req));
	}
	
	
	@PostMapping("/transferViaAccount")
	public ResponseEntity<TransferResponse> transferAmount(@RequestHeader("X-User-Id") String userId,@RequestBody TransferRequest req){
		return ResponseEntity.status(HttpStatus.OK).body(accService.transferAmount(userId,req));
	}
	
	
	@PostMapping("/transferViaEmail")
	public ResponseEntity<TransferResponse> transferAmountViaEmail(@RequestHeader("X-User-Id") String userId,@RequestBody TransferViaEmailRequest req){
		return ResponseEntity.status(HttpStatus.OK).body(accService.transferviaemail(userId,req));
	}
	
	
	@GetMapping("/transactionhistory/{accNumber}")
	public ResponseEntity<List<TransHistory>> getTransactionHistory(@PathVariable Long accNumber){
		return ResponseEntity.status(HttpStatus.OK).body(accService.getHistory(accNumber));
	}
	@PutMapping("/{accountNumber}/close")
	public ResponseEntity<String> closeAccount(
	        @RequestHeader("X-User-Id") String userId,
	        @PathVariable String accountNumber) {

	    return ResponseEntity.ok(accService.closeAccount(userId, accountNumber));
	}
	
	@PutMapping("/{accountNumber}/reopen")
	public ResponseEntity<String> reopenAccount(
	        @RequestHeader("X-User-Id") String userId,
	        @PathVariable String accountNumber) {

	    return ResponseEntity.ok(accService.reopenAccount(userId, accountNumber));
	}

}
