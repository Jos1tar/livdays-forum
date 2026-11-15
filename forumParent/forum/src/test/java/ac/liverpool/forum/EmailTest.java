package ac.liverpool.forum;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

// Sends a real email - needs MAIL_USERNAME / MAIL_PASSWORD. Remove @Disabled to run it manually.
@Disabled("manual test: sends a real email")
@SpringBootTest
public class EmailTest {

    @Autowired
    private JavaMailSender mailSender;

    @Test
    void testPersonalEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-account@163.com");
        message.setTo("receiver@example.com");
        message.setSubject("Personal Email Test");
        message.setText("This is a test email sent from the personal email account.");

        try {
            mailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (MailException e) {
            System.err.println("Failed to send email.");
            e.printStackTrace();
        }
    }
}
