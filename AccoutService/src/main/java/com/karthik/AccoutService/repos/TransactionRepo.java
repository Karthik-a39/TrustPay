package com.karthik.AccoutService.repos;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.karthik.AccoutService.Entities.AccountEntity;
import com.karthik.AccoutService.Entities.TransactionEntity;

public interface TransactionRepo  extends JpaRepository<TransactionEntity, Long>{
	List<TransactionEntity> findByAccountOrderByCreatedAtDesc(AccountEntity Account);
 
}
