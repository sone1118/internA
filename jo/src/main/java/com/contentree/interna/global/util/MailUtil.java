package com.contentree.interna.global.util;

import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 김지슬
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
@EnableAsync
public class MailUtil {
	
	private final JavaMailSender javaMailSender;
	private final SpringTemplateEngine templateEngine;
	
	private int certCharLength = 8;
    private final char[] characterTable = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 
                                            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
                                            'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
    
    // 문자 + 숫자 인증 코드 생성
    public String createRandomCode() {
    	log.info("MailUtil > createRandomCode - 호출");
    	
        Random random = new Random(System.currentTimeMillis());
        int tablelength = characterTable.length;
        StringBuffer buf = new StringBuffer();
        
        for(int i = 0; i < certCharLength; i++) {
            buf.append(characterTable[random.nextInt(tablelength)]);
        }
        
        return buf.toString();
    }
    
    @Async
    public void sendEmailToJoins(String joinsId, String randomCode) throws MessagingException{
    	log.info("MailUtil > sendEmailToJoins - 호출, 이메일로 임직원 인증 코드 전송 시도 (joinsId : {})", joinsId);
    	
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject("Jo! 중앙그룹 통합 로그인 서비스 임직원 인증 코드");
        helper.setTo(joinsId + "@joins.com");
        
        Context context = new Context();
        context.setVariable("randomCode", randomCode);
        String html = templateEngine.process("joins-email-authentication.html", context);
        helper.setText(html, true);
        
        javaMailSender.send(message);
        log.info("MailUtil > sendEmailToJoins - 이메일 전송 완료 (joinsId : {})", joinsId);
    }
    

}
