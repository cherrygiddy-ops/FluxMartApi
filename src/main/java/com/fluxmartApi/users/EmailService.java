package com.fluxmartApi.users;

public class EmailService {
    public void sendVerificationEmail(String email, String token) {
        String link = "https://fluxmart.com/verify?token=" + token;
        String body = "Click to verify your account: " + link;

        // Use JavaMailSender or any SMTP provider
//        mailSender.send(new SimpleMailMessage() {{
//            setTo(email);
//            setSubject("Verify your FluxMart account");
//            setText(body);
//        }});
    }

}
