package de.germanmatrimony.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Main method to call from services like AuthService, etc.
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, String> variables) {
        try {
            // ✅ Step 1: Load template from resources/templates
            String path = "src/main/resources/templates/" + templateName;
            String html = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);

            // ✅ Step 2: Replace all placeholders like ${otp}, ${introMessage}
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                html = html.replace("${" + entry.getKey() + "}", entry.getValue());
            }

            // ✅ Step 3: Prepare the MIME email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true); // true = HTML content

            // ✅ Step 4: Attach inline logo (optional)
            File logo = new File("src/main/resources/static/logo.png");
            if (logo.exists()) {
                helper.addInline("logo", new FileSystemResource(logo));
                System.out.println("✅ Inline logo attached.");
            } else {
                System.out.println("⚠️ No logo found — skipping inline logo.");
            }

            // ✅ Step 5: Send it
            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + to);

        } catch (IOException e) {
            System.out.println("❌ Template file error: " + e.getMessage());
        } catch (MessagingException e) {
            System.out.println("❌ Failed to send email (Messaging): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Unknown error while sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
