package com.karthik.NotificationService.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${notification.from-email}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String toEmail) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject("Welcome to TrustPay 🎉");
            msg.setText(
                "Hello,\n\n" +
                "We’re excited to let you know that your account has been created successfully with TrustPay.\n" +
                "You can now log in and start exploring our secure digital payment services.\n\n" +
                "👉 With TrustPay, you get:\n" +
                "- Real-time notifications for every transaction\n" +
                "- Safe and reliable account activity tracking\n" +
                "- Personalized updates tailored to your financial journey\n" +
                "- Bank-grade security for peace of mind\n\n" +
                "Thank you for choosing TrustPay to simplify your payments and manage your money smarter.\n\n" +
                "Best regards,\n" +
                "NotificationService Team\n" +
                "Powered by TrustPay"
            );

            mailSender.send(msg);
            log.info("Welcome email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void senddepositMsg(String toEmail, String amount, String newBalance) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Deposit Successful - TrustPay");
            message.setText("Hi,\n\n₹" + amount + " has been deposited into your account.\n"
                    + "Updated balance: ₹" + newBalance + "\n\n"
                    + "Thanks for banking with us.\n\n- TrustPay Team");

            mailSender.send(message);
            log.info("Deposit email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send deposit email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendWithdrawMsg(String toEmail, String amount, String newBalance) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Withdrawal Successful - TrustPay");
            message.setText("Hi,\n\n₹" + amount + " has been withdrawn from your account.\n"
                    + "Updated balance: ₹" + newBalance + "\n\n"
                    + "Thanks for banking with us.\n\n- TrustPay Team");

            mailSender.send(message);     
            log.info("Withdrawal email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send withdrawal email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendTransferMsg(String toEmail, String amount, String newBalance, String direction) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);

            String subject = direction.equals("SENT") ? "Money Sent - TrustPay" : "Money Received - TrustPay";
            String actionText = direction.equals("SENT") ? "sent from" : "received into";

            message.setSubject(subject);
            message.setText("Hi,\n\n₹" + amount + " has been " + actionText + " your account.\n"
                    + "Updated balance: ₹" + newBalance + "\n\n"
                    + "Thanks for banking with us.\n\n- TrustPay Team");

            mailSender.send(message);
            log.info("Transfer email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send transfer email to {}: {}", toEmail, e.getMessage());
        }
    }
}
