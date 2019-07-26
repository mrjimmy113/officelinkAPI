package com.fpt.officelink.mail.service;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;


@Service
public class MailService {
        @Autowired
        private JavaMailSender sender;

        @Autowired
        private Configuration config;

        @Async
        public void sendMail(String[] to,String temp , Map<String , Object> model){

                MimeMessage message = sender.createMimeMessage();
                try{
                        MimeMessageHelper helper = new  MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());


                        Template template = config.getTemplate(temp);
                        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template,model);
                        helper.setSubject("OfficeLink ");
                        helper.setTo(to);
                        helper.setText(html, true);
                        helper.setFrom("officelinksup@gmail.com");
                        sender.send(message);




                }catch (MessagingException | IOException | TemplateException ex){
                        ex.printStackTrace();

                }


        }





}
