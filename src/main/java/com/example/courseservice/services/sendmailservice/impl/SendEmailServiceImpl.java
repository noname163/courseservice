package com.example.courseservice.services.sendmailservice.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.courseservice.data.dto.request.SendMailRequest;
import com.example.courseservice.services.sendmailservice.SendEmailService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SendEmailServiceImpl implements SendEmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Override
    @Async
    public void sendMailService(SendMailRequest sendMailRequest) {
        MimeMessage massage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(massage);
            helper.setSubject(sendMailRequest.getSubject());
            helper.setFrom("CEPANoReply@gmail.com");
            helper.setTo(sendMailRequest.getUserEmail());
            helper.setText(sendMailRequest.getMailTemplate(), true);
            javaMailSender.send(massage);
            log.info("Send mail success for email {} ", sendMailRequest.getUserEmail());
        } catch (MessagingException e) {
             log.error(e.getMessage());
        }

    }

}
