package com.karthik.AccoutService.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.karthik.AccoutService.Entities.AccountEntity;
import com.karthik.AccoutService.Entities.TransactionEntity;
import com.karthik.AccoutService.dtos.AccountCreationResponse;
import com.karthik.AccoutService.dtos.AccountNumberGenerator;
import com.karthik.AccoutService.dtos.AccountRequestDto;
import com.karthik.AccoutService.dtos.DepositeRequest;
import com.karthik.AccoutService.dtos.DepositeResponse;
import com.karthik.AccoutService.dtos.TransHistory;
import com.karthik.AccoutService.dtos.TransactionIdGenerator;
import com.karthik.AccoutService.dtos.TransferRequest;
import com.karthik.AccoutService.dtos.TransferResponse;
import com.karthik.AccoutService.dtos.TransferViaEmailRequest;
import com.karthik.AccoutService.dtos.UserResponse;
import com.karthik.AccoutService.dtos.VerifyPinRequest;
import com.karthik.AccoutService.dtos.WithDrawResponse;
import com.karthik.AccoutService.dtos.WithdrawRequest;
import com.karthik.AccoutService.enums.AccStatus;
import com.karthik.AccoutService.enums.TransactionType;
import com.karthik.AccoutService.openFeignClients.EmailClient;
import com.karthik.AccoutService.repos.AccountRepo;
import com.karthik.AccoutService.repos.TransactionRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServices {

	private final AccountRepo accountRepo;
	private final AccountNumberGenerator accGenerate;
	private final EmailClient eClient;
	private final KafkaTemplate<String, String> kafkamsg;
	private final TransactionIdGenerator transGenerate;
	private final TransactionRepo transRepo;

	
	
	public AccountCreationResponse createAccount(String token, String userId, AccountRequestDto req) {
	    AccountEntity account = toEntity(req);
	    account.setAccountNumber(accGenerate.getnumber());
	    account.setBalance(BigDecimal.ZERO);
	    account.setUserId(userId);
	    account.setAccountStatus(AccStatus.ACTIVE);

	    accountRepo.save(account);

	    String email = eClient.getEmail(token);
	    kafkamsg.send("accountCreated", email);

	    return toResponse(account);
	}
	
	public AccountCreationResponse getByUserId(String userId) {
		AccountEntity  account=accountRepo.findByUserId(userId).orElse(null);
		if(account==null) {
			throw new RuntimeException("Account Not Found ! Please create Account");
		}
		
		return toResponse(account);
	}

	public Map<String,BigDecimal> getBalanceByAccId(String accId){
		BigDecimal balance=accountRepo.findBalanceByAccountNumber(accId).orElse(null);
		if(balance==null) {
			throw new RuntimeException("Enter Correct Account Number"); 
		}
		Map<String,BigDecimal> mapp=new HashMap<>();
		mapp.put("balance", balance);
		return mapp;
	}
	
	
	
	public DepositeResponse depositAmmount(String accountNumber,DepositeRequest req) {
		AccountEntity userAccount=accountRepo.findByAccountNumber(accountNumber).orElseThrow(
				()-> new RuntimeException("Account does not exist with this account Number "));
		
		
		
		System.out.println("DEBUG accountNumber=" + userAccount.getAccountNumber());
	    System.out.println("DEBUG balance=" + userAccount.getBalance());
	
		BigDecimal depositAmount=req.getAmount();
		
		 if (depositAmount == null || depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
		        throw new RuntimeException("Deposit amount must be positive");
		    }
		System.out.println("DEBUG depositAmount=" + depositAmount); 
		
		BigDecimal newBalance=userAccount.getBalance().add(depositAmount);
		
		userAccount.setBalance(newBalance);
		accountRepo.save(userAccount);
		
		TransactionEntity transaction=TransactionEntity.builder()
				.account(userAccount)
				.amount(depositAmount)
				.transactionId(transGenerate.getnumber())
				.transactionType(TransactionType.DEPOSIT)
				.balanceAfter(newBalance)
				.description(req.getDescription())
				.build();
		
		transRepo.save(transaction);
		
		String email=eClient.getUseeByuserId(userAccount.getUserId()).getEmail();
		
		String message = email + "|" + depositAmount.toString() + "|" + newBalance.toString();
	    kafkamsg.send("amount-deposit", message);
		
		
		
		return DepositeResponse.builder()
				.transactionId(transaction.getTransactionId())
				.balanceAfter(newBalance)
				.build(); 
				
		
		              
			                
			                 
		
	}
	

	

	public WithDrawResponse withDrawAmount(String accNumber, WithdrawRequest req) {
		AccountEntity userAccount=accountRepo.findByAccountNumber(accNumber).orElseThrow(
				()-> new RuntimeException("Account does not exist with this account Number "));
		
		
		System.out.println("DEBUG accountNumber=" + userAccount.getAccountNumber());
	    System.out.println("DEBUG balance=" + userAccount.getBalance());
	
		BigDecimal withDrawAmount=req.getAmount();
		 if (withDrawAmount == null || withDrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
		        throw new RuntimeException("Withdrawal amount must be positive");
		    }
		System.out.println("DEBUG WithDrawAmount=" + withDrawAmount); 
		
		BigDecimal newBalance=userAccount.getBalance().subtract(withDrawAmount);
		
		userAccount.setBalance(newBalance);
		accountRepo.save(userAccount);
		
		TransactionEntity transaction=TransactionEntity.builder()
				.account(userAccount)
				.amount(withDrawAmount)
				.transactionId(transGenerate.getnumber())
				.transactionType(TransactionType.WITHDRAW)
				.balanceAfter(newBalance)
				.description(req.getDescription())
				.build();
		
		transRepo.save(transaction);
		
		String email=eClient.getUseeByuserId(userAccount.getUserId()).getEmail();
		
		String message = email + "|" + withDrawAmount.toString() + "|" + newBalance.toString();
	    kafkamsg.send("amount-withdraw", message);
		
		
		
		return WithDrawResponse.builder()
				.transactionId(transaction.getTransactionId())
				.balanceAfter(newBalance)
				.build(); 
				
	}
	
	
	
	
	private AccountCreationResponse toResponse(AccountEntity account) {
		// TODO Auto-generated method stub
		return AccountCreationResponse.builder()
				.id(account.getId())
				.balance(account.getBalance())
				.accountNumber(account.getAccountNumber())
				.accountStatus(account.getAccountStatus())
				.accountType(account.getAccountType())
				.createdAt(account.getCreatedAt())
				.userId(account.getUserId())
				.build();
	}


	private AccountEntity toEntity(AccountRequestDto req) {
		return AccountEntity.builder()
				.accountStatus(req.getAccountStatus())
				.accountType(req.getAccountType())
				.balance(BigDecimal.ZERO)
				.createdAt(req.getCreatedAt())
				.id(req.getId())
				.build();
			    
	}

	@Transactional
	public TransferResponse transferAmount(String userId, TransferRequest req) {
		AccountEntity senderAccount=accountRepo.findByUserId(userId).orElseThrow(
				()-> new RuntimeException(" Sender Account is Not Exsit"));
		
		AccountEntity receiverAccount=accountRepo.findByAccountNumber(req.getToAccountNumber()).orElseThrow(
				  ()-> new RuntimeException("receiver Account is Not Exsit"));
		
		
		
		if(senderAccount.getAccountNumber().equals(receiverAccount.getAccountNumber())) {
			throw new RuntimeException("Sender and reciver account number must be Different!");
		}
		if (senderAccount.getAccountStatus() != AccStatus.ACTIVE || receiverAccount.getAccountStatus()!=AccStatus.ACTIVE) {
		    throw new RuntimeException("Accounts are not  active Reopen for Transaction . Current status: " + senderAccount.getAccountStatus());
		}
		BigDecimal senderBalance=senderAccount.getBalance();
		BigDecimal amount=req.getAmount();
		if(senderBalance.compareTo(amount)<0) {
			throw new RuntimeException("Insufficient Amount!");
		}
		VerifyPinRequest pinReq = new VerifyPinRequest(senderAccount.getId(), req.getPin());
		boolean valid = eClient.verifyPin(pinReq);
		if (!valid) {
		    throw new RuntimeException("Invalid transaction PIN");
		}
		
		 BigDecimal senderNewBalance = senderAccount.getBalance().subtract(amount);
		    senderAccount.setBalance(senderNewBalance);
		    accountRepo.save(senderAccount);

		    // credit receiver
		    BigDecimal receiverNewBalance = receiverAccount.getBalance().add(amount);
		    receiverAccount.setBalance(receiverNewBalance);
		    accountRepo.save(receiverAccount);
		    
		    
		    TransactionEntity debitTxn = TransactionEntity.builder()
		            .account(senderAccount)
		            .amount(amount)
		            .transactionId(transGenerate.getnumber())
		            .transactionType(TransactionType.TRANSFER_OUT)
		            .balanceAfter(senderNewBalance)
		            .relatedAccount(receiverAccount.getAccountNumber())
		            .description(req.getDescription())
		            .build();

		    TransactionEntity creditTxn = TransactionEntity.builder()
		            .account(receiverAccount)
		            .amount(amount)
		            .transactionId(transGenerate.getnumber())
		            .transactionType(TransactionType.TRANSFER_IN)
		            .balanceAfter(receiverNewBalance)
		            .relatedAccount(senderAccount.getAccountNumber())
		            .description(req.getDescription())
		            .build();

		    transRepo.save(debitTxn);
		    transRepo.save(creditTxn);
		    
		    
		    
		    // notify both sender and receiver via Kafka
		    String senderEmail = eClient.getUseeByuserId(senderAccount.getUserId()).getEmail();
		    String receiverEmail = eClient.getUseeByuserId(receiverAccount.getUserId()).getEmail();

		    String senderMsg = senderEmail + "|" + amount + "|" + senderNewBalance + "|SENT";
		    String receiverMsg = receiverEmail + "|" + amount + "|" + receiverNewBalance + "|RECEIVED";

		    kafkamsg.send("amount-transfer", senderMsg);
		    kafkamsg.send("amount-transfer", receiverMsg);

		    return TransferResponse.builder()
		            .transactionId(debitTxn.getTransactionId())
		            .fromAccountNumber(senderAccount.getAccountNumber())
		            .toAccountNumber(receiverAccount.getAccountNumber())
		            .amount(amount)
		            .senderBalanceAfter(senderNewBalance)
		            .build();
		
		
	}

	public List<TransHistory> getHistory(Long accNumber) {
		
		AccountEntity account=accountRepo.findById(accNumber).orElseThrow(()-> new RuntimeException("account does not exist!"));
		
	    List<TransactionEntity> transactions=transRepo.findByAccountOrderByCreatedAtDesc(account);
	    if(transactions==null) {
	    	throw new RuntimeException("No Transactions Are Done Yet!!");
	    }
	    
	    return transactions.stream()
	    		.map(this::tohistory).toList();
	}
	
	
	private TransHistory tohistory(TransactionEntity en) {
		return TransHistory.builder()
				.id(en.getId())
				.account(en.getAccount())
				.amount(en.getAmount())
				.balanceAfter(en.getBalanceAfter())
				.description(en.getDescription())
				.transfeAt(en.getCreatedAt())
				.relatedAccount(en.getRelatedAccount())
				.transactionId(en.getTransactionId())
				.build();
	}

	
	@Transactional
	public TransferResponse transferviaemail(String userId, TransferViaEmailRequest req) {
		AccountEntity  senderAccount=accountRepo.findByUserId(userId).orElseThrow(()->new RuntimeException("Sender Account Not Found"));
		
		UserResponse res=eClient.getByEmail(req.getEmail());
		AccountEntity receiverAccount=accountRepo.findByUserId(res.getUserId()).orElseThrow(()->new RuntimeException("Receiver Account Not Found"));
		
		if(senderAccount.getUserId().equals(receiverAccount.getUserId())) {
			throw new RuntimeException("Enter Receiver Email correctly!");
		}
		
		if (senderAccount.getAccountStatus() != AccStatus.ACTIVE || receiverAccount.getAccountStatus()!=AccStatus.ACTIVE) {
		    throw new RuntimeException("Accounts are not  active Reopen for Transaction . Current status: " + senderAccount.getAccountStatus());
		}
		
		VerifyPinRequest pinReq = new VerifyPinRequest(senderAccount.getId(), req.getPin());
		boolean valid = eClient.verifyPin(pinReq);
		if (!valid) {
		    throw new RuntimeException("Invalid transaction PIN");
		}
		
	    BigDecimal amount=req.getAmount();
	    if(senderAccount.getBalance().compareTo(amount)<0) {
			throw new RuntimeException("Insufficient Amount!");
		}
	    
	    BigDecimal senderNewBalance=senderAccount.getBalance().subtract(amount);
	    senderAccount.setBalance(senderNewBalance);
	    accountRepo.save(senderAccount);
	    
	    
	    BigDecimal reciverNewBalance=receiverAccount.getBalance().add(amount);
	    receiverAccount.setBalance(reciverNewBalance);
	    accountRepo.save(receiverAccount);
	    
	    
	    TransactionEntity dtxn=TransactionEntity.builder()
	    		.transactionId(transGenerate.getnumber())
	    		.account(senderAccount)
	    		.amount(amount)
	    		.balanceAfter(senderNewBalance)
	    		.description(req.getDescription())
	    		.transactionType(TransactionType.TRANSFER_OUT)
	    		.relatedAccount(receiverAccount.getUserId())
	    		.build();
	    
	    
	    
	    TransactionEntity crtxn=TransactionEntity.builder()
	    		.transactionId(transGenerate.getnumber())
	    		.account(receiverAccount)
	    		.amount(amount)
	    		.balanceAfter(reciverNewBalance)
	    		.description(req.getDescription())
	    		.transactionType(TransactionType.TRANSFER_IN)
	    		.relatedAccount(senderAccount.getUserId())
	    		.build();
	    
	    
	    transRepo.save(dtxn);
	    transRepo.save(crtxn);
	    
	    return TransferResponse.builder()
	            .transactionId(dtxn.getTransactionId())
	            .fromAccountNumber(senderAccount.getAccountNumber())
	            .toAccountNumber(receiverAccount.getAccountNumber())
	            .amount(amount)
	            .senderBalanceAfter(senderNewBalance)
	            .build();
	    
	    
	    
	}

	public String closeAccount(String userId, String accountNumber) {
	    AccountEntity account = accountRepo.findByAccountNumber(accountNumber)
	            .orElseThrow(() -> new RuntimeException("Account does not exist"));

	    // make sure the person closing the account actually owns it
	    if (!account.getUserId().equals(userId)) {
	        throw new RuntimeException("You are not authorized to close this account");
	    }

	    if (account.getAccountStatus() == AccStatus.CLOSED) {
	        throw new RuntimeException("Account is already closed");
	    }

	    // don't allow closing an account with money still in it — common real-world rule
	    if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
	        throw new RuntimeException("Cannot close account with remaining balance. Please withdraw or transfer funds first.");
	    }

	    account.setAccountStatus(AccStatus.CLOSED);
	    accountRepo.save(account);

	    return "Account closed successfully";
	}

	public String reopenAccount(String userId, String accountNumber) {
	    AccountEntity account = accountRepo.findByAccountNumber(accountNumber)
	            .orElseThrow(() -> new RuntimeException("Account does not exist"));

	    if (!account.getUserId().equals(userId)) {
	        throw new RuntimeException("You are not authorized to reopen this account");
	    }

	    account.setAccountStatus(AccStatus.ACTIVE);
	    accountRepo.save(account);
	    return "Account reopened successfully";
	}

}
