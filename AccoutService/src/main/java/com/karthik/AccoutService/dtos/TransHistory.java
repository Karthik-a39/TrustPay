package com.karthik.AccoutService.dtos;



import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.karthik.AccoutService.Entities.AccountEntity;
import com.karthik.AccoutService.enums.TransactionType;

import jakarta.persistence.*;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransHistory {

  
    private Long id;

  
    private AccountEntity account;
    
    
    private Long transactionId;

    
    private TransactionType transactionType;

   
    private BigDecimal amount;

   
    private BigDecimal balanceAfter;

    // nullable — only set for TRANSFER_OUT / TRANSFER_IN
    private String relatedAccount;

    // nullable — "rent payment" etc.
    private String description;

   
    private LocalDateTime transfeAt;

   
}
