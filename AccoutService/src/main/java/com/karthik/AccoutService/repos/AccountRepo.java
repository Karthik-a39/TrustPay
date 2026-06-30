package com.karthik.AccoutService.repos;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.karthik.AccoutService.Entities.AccountEntity;

import feign.Param;
@Repository
public interface AccountRepo extends JpaRepository<AccountEntity, Long> {

	Optional<AccountEntity> findByUserId(String userId);
	
	@Query("SELECT a.balance FROM AccountEntity a WHERE a.accountNumber = :accountNumber")
    Optional<BigDecimal> findBalanceByAccountNumber(@Param("accountNumber") String accountNumber);
	
	
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
}
