package com.karthik.NotificationService.services;


import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerClass {

    private static final Logger log = LoggerFactory.getLogger(KafkaListenerClass.class);

    private final EmailService emailService;

    public KafkaListenerClass(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "accountCreated", groupId = "${spring.kafka.consumer.group-id}")
    public void getmsg(String email) {
    	String cleanEmail = email.replaceAll("^\"|\"$", "").trim();
        emailService.sendWelcomeEmail(cleanEmail);
    }
    
    
    
    @KafkaListener(topics = "amount-deposit", groupId = "${spring.kafka.consumer.group-id}")
    public void getDepositmsg(String msg) {
        log.info("Received deposit event: {}", msg);

        String cleanMsg = msg.replaceAll("^\"|\"$", "").trim();  // strip leading/trailing quotes
        String[] parts = cleanMsg.split("\\|");

        if (parts.length < 3) {
            log.error("Malformed deposit message: {}", cleanMsg);
            return;
        }

        String email = parts[0].trim();
        String amount = parts[1].trim();
        String newBalance = parts[2].trim();

        emailService.senddepositMsg(email, amount, newBalance);
    }
    
    
    @KafkaListener(topics = "amount-withdraw", groupId = "${spring.kafka.consumer.group-id}")
    public void getWithdrawMsg(String msg) {
        log.info("Received withdrawal event: {}", msg);

        String cleanMsg = msg.replaceAll("^\"|\"$", "").trim();
        String[] parts = cleanMsg.split("\\|"); 

        if (parts.length < 3) {
            log.error("Malformed withdrawal message: {}", cleanMsg);
            return;
        }

        String email = parts[0].trim();
        String amount = parts[1].trim();
        String newBalance = parts[2].trim();

        emailService.sendWithdrawMsg(email, amount, newBalance);
    }
    
    @KafkaListener(topics = "amount-transfer", groupId = "${spring.kafka.consumer.group-id}")
    public void getTransferMsg(String msg) {
        log.info("Received transfer event: {}", msg);

        String cleanMsg = msg.replaceAll("^\"|\"$", "").trim();
        String[] parts = cleanMsg.split("\\|");

        if (parts.length < 4) {
            log.error("Malformed transfer message: {}", cleanMsg);
            return;
        }

        String email = parts[0].trim();
        String amount = parts[1].trim();
        String newBalance = parts[2].trim();
        String direction = parts[3].trim();

        emailService.sendTransferMsg(email, amount, newBalance, direction);
    }
}
