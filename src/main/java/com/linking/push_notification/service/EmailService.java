package com.linking.push_notification.service;

import com.linking.push_notification.domain.NoticeType;
import com.linking.push_notification.domain.PushNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async("eventCallExecutor")
    public void sendEmail(PushNotification push) {
        Context context = new Context();
        context.setVariable("receiver", push.getUser().getFullName());
        context.setVariable("sender", push.getSender());
        context.setVariable("projectName", push.getProject().getProjectName());
        context.setVariable("title", push.getBody());
        if (push.getNoticeType().equals(NoticeType.PAGE)) {
            context.setVariable("type", "Page");
            context.setVariable("temp", " 확인 요청");
        } else {
            context.setVariable("type", "Todo");
            context.setVariable("temp", " 완료 요청");
        }

        String htmlMessage = templateEngine.process("email.html", context);

        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null; // 2번째 인자는 multipart 여부

        try {
            mimeMessageHelper = new MimeMessageHelper(mail, true, "UTF-8");
            mimeMessageHelper.setFrom(new InternetAddress("saessack2019@gmail.com", "Linking", "UTF-8"));
            mimeMessageHelper.setTo(push.getUser().getEmail());
            mimeMessageHelper.setSubject("사용자 요청 메일");
            mimeMessageHelper.setText(htmlMessage, true);
            mimeMessageHelper.addInline("logo", new ClassPathResource("static/img/linking-logo.png"));
            javaMailSender.send(mail);

        }catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            log.info("mail delivery failed");
        } catch (MessagingException e) {
            log.error(e.getMessage());
            log.info("mail delivery failed");
        }
    }
}
