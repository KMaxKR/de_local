package ks.mx.de_local.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class EmailService {
    private final Logger logger = LoggerFactory.getLogger("EmailService");
    private final Random random = new Random();
    private final ValidationService validationService;

    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;

    private final int MIN_VALUE = 1000;
    private final int MAX_VALUE = 9999;

    public void sendConfirmationCode(String to){
        Properties prop = new Properties();
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable","true");
        prop.put("mail.smtp.EnableSSL.enable","true");

        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSentDate(new Date());

            // TODO auto generation code and db connection with user and code
            int code = random.nextInt(MAX_VALUE - MIN_VALUE + 1) + MIN_VALUE;
            String content = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Welcome to KS.MSX!</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f4f4f4;\n" +
                "            color: #333;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            background-color: #ffffff;\n" +
                "            border: 1px solid #ddd;\n" +
                "            border-radius: 10px;\n" +
                "            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .code {\n" +
                "            background-color: #4CAF50;\n" +
                "            color: #fff;\n" +
                "            font-size: 24px;\n" +
                "            font-weight: bold;\n" +
                "            padding: 10px 20px;\n" +
                "            border-radius: 5px;\n" +
                "            margin: 20px 0;\n" +
                "            display: inline-block;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            margin-top: 20px;\n" +
                "            font-size: 14px;\n" +
                "            color: #888;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>Welcome to KS.MSX!</h1>\n" +
                "        <p>Thank you for signing up with us. To complete your registration, please use the code below:</p>\n" +
                "        <div class=\"code\">" + code + "</div>\n" +
                "        <p>If you didn't request this, please ignore this email.</p>\n" +
                "        <div class=\"footer\">Thanks for using us!<br> - The KS.MSX Team</div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
            message.setContent(content, "text/html");
            message.setSubject("Confirmation Code");
            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());

            validationService.saveValidation(to, code);

            logger.info("Code {} was sent to user ", code);
        }catch (Exception e){
            logger.info("Exception with {}", e.getMessage());
        }
    }
}
