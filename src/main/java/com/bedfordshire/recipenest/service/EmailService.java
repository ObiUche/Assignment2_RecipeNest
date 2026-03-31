package com.bedfordshire.recipenest.service;

import com.bedfordshire.recipenest.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    // Spring Boot mail sender bean
    private final JavaMailSender mailSender;

    // Frontend base URL from application.properties
    @Value("${app.frontend-url}")
    private String frontendUrl;



    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendEmailVerification(User user, String token){
        // Builds the verification link the user will click
        String verificationLink = frontendUrl + "/verify-email?token=" + token;

        // Simple plain-text email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Verify your Email");
        message.setText(
                "Hi " + user.getFirstName() + ",\n\n" +
                        "Please verify your email by clicking the link below: \n" +
                        verificationLink + "\n\n" +
                        "If you did not create this account, please ignore this email."

        );

        // Sends the email
        mailSender.send(message);
    }

    public void sendPasswordReset(User user, String token){
        // Builds the reset password link the user will click
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        // Simple plain-text email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Reset your password");
        message.setText(
                "Hi " + user.getFirstName() + ",\n\n" +
                        "You requested a password reset." +
                        " Click the link below to set a new password :\n" +
                        resetLink + "\n\n" +
                        "If you did not request this, please ignore this email."
        );

        // Sends the email
        mailSender.send(message);
    }
}
