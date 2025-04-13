package com.policene.voluntech.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String organizationName, String subject, String status) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);

            String template;

            if (status.equals("Approved")) {
                template = loadApprovedOrganizationTemplate();
                template = template.replace("{name}", organizationName);
                template = template.replace("{organizationName}", organizationName);
                helper.setText(template, true);
                mailSender.send(message);

            } else if (status.equals("Rejected")) {
                template = loadRejectedOrganizationTemplate();
                template = template.replace("{name}", organizationName);
                template = template.replace("{organizationName}", organizationName);
                helper.setText(template, true);
                mailSender.send(message);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String loadApprovedOrganizationTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("approved-email-template.html");
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public String loadRejectedOrganizationTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("rejected-email-template.html");
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

}
