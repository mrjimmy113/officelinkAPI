package com.fpt.officelink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class SendMail {

        @Autowired
        private MailSender mailSender;




        public void sendMail(String[] emailTo , String text)
        {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(emailTo);
                message.setSubject("OfficeLink");
                message.setText(text);
                mailSender.send(message);
        }

}


