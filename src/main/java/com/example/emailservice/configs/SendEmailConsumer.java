package com.example.emailservice.configs;

import com.example.emailservice.EmailUtils.EmailUtil;
import com.example.emailservice.dtos.UserKafkaDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class SendEmailConsumer {
    private ObjectMapper mapper;
    private EmailUtil emailUtil;
    public SendEmailConsumer(ObjectMapper objectMapper, EmailUtil emailUtil) {
        this.mapper = objectMapper;
        this.emailUtil = emailUtil;

    }
    @KafkaListener(topics = "sendEmail",groupId = "emailservice")
    public void handleSendEmail(String message) throws JsonProcessingException {
        UserKafkaDto userKafkaDto = (UserKafkaDto) mapper.readValue(message, UserKafkaDto.class);
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userKafkaDto.getFrom(), "eavotjyysfmdioym");
            }
        };
        Session session = Session.getInstance(props, auth);

        emailUtil.sendEmail(session, userKafkaDto.getTo(),userKafkaDto.getSub(), userKafkaDto.getMessage());
    }
}
