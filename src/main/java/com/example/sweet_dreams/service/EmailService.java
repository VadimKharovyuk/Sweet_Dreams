package com.example.sweet_dreams.service;

import com.example.sweet_dreams.model.Subscriber;
import com.example.sweet_dreams.repository.SubscriberRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender emailSender;
    private final SubscriberRepository subscriberRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true для HTML

            emailSender.send(message);
            log.info("Email successfully sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendSubscriptionConfirmation(String email) {
        String subject = "Підтвердження підписки - Sweet Dreams";
        String content = createSubscriptionConfirmationEmail();
        sendEmail(email, subject, content);
    }

    private String createSubscriptionConfirmationEmail() {
        return """
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { background-color: #4f46e5; color: white; padding: 20px; text-align: center; }
                .content { padding: 20px; }
                .footer { text-align: center; padding: 20px; color: #666; }
                .button { 
                    display: inline-block;
                    padding: 10px 20px;
                    background-color: #4f46e5;
                    color: white;
                    text-decoration: none;
                    border-radius: 5px;
                    margin-top: 20px;
                }
                .benefits {
                    background-color: #f8fafc;
                    padding: 20px;
                    border-radius: 8px;
                    margin: 20px 0;
                }
                .benefits ul {
                    list-style-type: none;
                    padding: 0;
                }
                .benefits li {
                    padding: 8px 0;
                    padding-left: 24px;
                    position: relative;
                }
                .benefits li:before {
                    content: "✓";
                    color: #4f46e5;
                    position: absolute;
                    left: 0;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Sweet Dreams</h1>
                </div>
                <div class="content">
                    <h2>Дякуємо за підписку!</h2>
                    <p>Ви успішно підписались на розсилку Sweet Dreams.</p>
                    
                    <div class="benefits">
                        <p>Тепер ви будете отримувати:</p>
                        <ul>
                            <li>Новини про нові продукти та акції</li>
                            <li>Спеціальні пропозиції та знижки</li>
                            <li>Рецепти та корисні поради</li>
                            <li>Ексклюзивні пропозиції для підписників</li>
                        </ul>
                    </div>
                    
                    <p>Щоб не пропустити наші новини, додайте нашу адресу в список контактів.</p>
                    
                    <div style="text-align: center;">
                        <a href="http://localhost:1001" class="button">
                            Перейти на сайт
                        </a>
                    </div>
                </div>
                <div class="footer">
                    <p>З повагою,<br>команда Sweet Dreams</p>
                    <small>
                        Якщо ви хочете відписатися від розсилки, 
                        <a href="http://localhost:1001/unsubscribe">натисніть тут</a>
                    </small>
                </div>
            </div>
        </body>
        </html>
        """;
    }

    public void sendNewsletterToAllSubscribers(String subject, String content) {
        List<Subscriber> activeSubscribers = subscriberRepository.findAllByActiveTrue();

        for (Subscriber subscriber : activeSubscribers) {
            try {
                sendEmail(subscriber.getEmail(), subject, content);
                Thread.sleep(100); // Небольшая задержка между отправками
            } catch (Exception e) {
                log.error("Failed to send newsletter to: {}", subscriber.getEmail(), e);
            }
        }
    }
    // Метод для создания шаблона новостной рассылки
    public String createNewsletterTemplate(String title, String mainContent) {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4f46e5; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .footer { text-align: center; padding: 20px; color: #666; }
                    .button { 
                        display: inline-block;
                        padding: 10px 20px;
                        background-color: #4f46e5;
                        color: white;
                        text-decoration: none;
                        border-radius: 5px;
                        margin-top: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s</h1>
                    </div>
                    <div class="content">
                        %s
                    </div>
                    <div class="footer">
                        <p>З повагою, команда Sweet Dreams</p>
                        <small>Якщо ви хочете відписатися від розсилки, 
                               <a href="http://localhost:1001/unsubscribe">натисніть тут</a>
                        </small>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(title, mainContent);
    }
}