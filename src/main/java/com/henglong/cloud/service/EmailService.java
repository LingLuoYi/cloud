package com.henglong.cloud.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String Sender;

    public void mail(String email, String s,String title) {
        log.info("发送身份确认邮件给【"+email+"】");
            MimeMessage message = null;
            try {
                message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(Sender);
                helper.setTo(email);
                helper.setSubject(title);

                helper.setText(s, true);
            } catch (Exception e) {
                log.error("邮件发送失败{}",e);
            }
            mailSender.send(message);
    }
}
