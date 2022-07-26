package com.work.covid19apiv2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();

        //this is the sender's email
        message.setFrom("joshuaamarfio1@gmail.com");
        //set receiver's email
        message.setTo(toEmail);
        //set the subject of the email
        message.setSubject(subject);
        //set the email body
        message.setText(body);

        mailSender.send(message);

        //prompt in the console that email has been sent
        System.out.println("Email Sent Successfully...");
    }
}
