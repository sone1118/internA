package com.contentree.interna.user.service;

import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.contentree.interna.global.util.CookieUtil;
import com.contentree.interna.global.util.JwtTokenUtil;
import com.contentree.interna.global.util.MailUtil;
import com.contentree.interna.global.util.RedisUtil;
import com.contentree.interna.user.controller.MypageContoller;
import com.contentree.interna.user.entity.Joins;
import com.contentree.interna.user.repository.JoinsRepository;
import com.contentree.interna.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MypageService {
	
	@Value("${spring.validation.validation-exiration}")
    private Integer validationExpiration;
	
	private final UserRepository userRepository;
	private final JoinsRepository joinsRepository;
	
	private final MailUtil mailUtil;
	private final RedisUtil redisUtil;
	
	public String sendEmailToJoins(Long userSeq, String joinsId) {
		log.info("MypageService > sendEmailToJoins - 호출 (userSeq : {})", userSeq);
		
		// 1. 이미 임직원 인증한 회원인지 확인
		Optional<Joins> joinsById = joinsRepository.findById(userSeq);
		if(joinsById.isPresent()) {
			return joinsById.get().getJoinsId();
		}
		
		// 2. 해당 아이디로 이미 인증한적 있는지 확인
		Optional<Joins> joinsByJoinsId = joinsRepository.findByJoinsId(joinsId);
		if(joinsByJoinsId.isPresent()) {
			return "2";
		}
		
		// 3. 이메일 전송 
		String randomCode = mailUtil.createRandomCode();
		try {
			mailUtil.sendEmailToJoins(joinsId, randomCode);
		} catch (MessagingException e) {
			log.error("MypageService > sendEmailToJoins - 이메일 전송 실패 (userSeq : {}, joinsId : {})", userSeq, joinsId);
			return "3";
		}
		
		// 4. 인증번호 레디스에 저장 
		redisUtil.setDataWithExpire("joins-" + joinsId, randomCode, validationExpiration);
		return "성공";
	}

	

}
