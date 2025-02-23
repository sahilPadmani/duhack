package tech.duhacks.duhacks.service;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tech.duhacks.duhacks.repository.HealthProductRepo;

@Service
@AllArgsConstructor
public class MailSenderService {

    private final JavaMailSender mailSender;
    private final HealthProductRepo healthProductRepo;

    public void SendEmail(Long healthProductId){
        var healthProduct = healthProductRepo.findById(healthProductId).orElse(null);

        if(healthProduct == null)
            return;


        var user = healthProduct.getUser();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String htmlContent = getExpiryHtmlTemplate(healthProduct.getName(), healthProduct.getExpiryDate().toString());

            helper.setTo(user.getEmail());
            helper.setSubject("Your health product is Expiring!!!!!");
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            System.out.printf("Error while sending email: %s%n", e.getMessage());
        }

    }


    private String getExpiryHtmlTemplate(String name, String date) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Medicine Expiry Reminder</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 0;
                        }
                        .email-container {
                            max-width: 600px;
                            margin: 20px auto;
                            background-color: #ffffff;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                            text-align: center;
                        }
                        .header {
                            background-color: #28a745;
                            color: white;
                            padding: 15px;
                            font-size: 20px;
                            font-weight: bold;
                            border-radius: 10px 10px 0 0;
                        }
                        .content {
                            padding: 20px;
                            font-size: 16px;
                            color: #333;
                        }
                        .medicine-name {
                            font-size: 22px;
                            font-weight: bold;
                            color: #d9534f;
                        }
                        .expiry-days {
                            font-size: 18px;
                            color: #ff9800;
                            font-weight: bold;
                        }
                        .footer {
                            margin-top: 20px;
                            font-size: 14px;
                            color: #777;
                        }
                    </style>
                </head>
                <body>
                    <div class="email-container">
                        <div class="header">Medicine Expiry Alert</div>
                        <div class="content">
                            <p>Hello,</p>
                            <p>Your medicine <span class="medicine-name">%s</span> is going to expire in <span class="expiry-days">%s days</span>.</p>
                            <p>Please ensure you use it before the expiration date or replace it as needed.</p>
                        </div>
                        <div class="footer">
                            <p>Stay healthy and take care!</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(name,date);
    }

}
