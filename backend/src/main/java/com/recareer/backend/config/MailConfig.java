package com.recareer.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 이메일 전송을 위한 설정 클래스
 * 
 * 프로필별로 다른 메일 설정을 제공합니다.
 * - dev: 테스트용 Gmail SMTP 설정
 * - prod: 실제 운영용 메일 설정
 */
@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    /**
     * 개발 환경용 JavaMailSender 빈 생성
     * 
     * 실제 이메일 서버 연결 없이도 애플리케이션이 시작되도록 설정합니다.
     * 실제 이메일 전송 시에는 연결 오류가 발생할 수 있지만,
     * 큐 시스템에 의해 재시도됩니다.
     * 
     * @return JavaMailSender 인스턴스
     */
    @Bean
    @Profile("dev")
    public JavaMailSender javaMailSenderDev() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "3000");
        props.put("mail.smtp.writetimeout", "5000");
        
        // 개발 환경에서는 SSL 검증을 비활성화 (테스트용)
        props.put("mail.smtp.ssl.trust", "*");

        return mailSender;
    }

    /**
     * 운영 환경용 JavaMailSender 빈 생성
     * 
     * 실제 운영 환경에서 사용할 메일 서버 설정입니다.
     * 보안 설정이 더 강화되어 있습니다.
     * 
     * @return JavaMailSender 인스턴스
     */
    @Bean
    @Profile("prod")
    public JavaMailSender javaMailSenderProd() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        return mailSender;
    }
}