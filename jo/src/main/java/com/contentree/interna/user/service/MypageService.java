package com.contentree.interna.user.service;

import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.contentree.interna.global.model.BusinessException;
import com.contentree.interna.global.model.ErrorCode;
import com.contentree.interna.global.util.MailUtil;
import com.contentree.interna.global.util.RedisUtil;
import com.contentree.interna.user.entity.Joins;
import com.contentree.interna.user.repository.JoinsRepository;

import groovyjarjarantlr4.v4.parse.ANTLRParser.throwsSpec_return;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 김지슬
 *
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MypageService {
	
	@Value("${spring.validation.validation-exiration}")
    private Integer validationExpiration;
	
	private final JoinsRepository joinsRepository;
	
	private final MailUtil mailUtil;
	private final RedisUtil redisUtil;
	
	// [ 김지슬 ] 임직원 인증 인증번호 전송
	public Boolean sendEmailToJoins(Long userSeq, String joinsId) {
		log.info("MypageService > sendEmailToJoins - 호출 (userSeq : {})", userSeq);
		
		// 1. 이미 임직원 인증한 회원인지 확인
		Optional<Joins> joinsById = joinsRepository.findById(userSeq);
		if(joinsById.isPresent()) {
			log.error("MypageService > sendEmailToJoins - 이미 인증된 사용자 (joinsId : {}, userSeq : {})", joinsId, userSeq);
			throw new BusinessException(ErrorCode.ALREADY_CERTIFIED, "이미 인증된 사용자입니다.");
		}
		
		// 2. 해당 아이디로 이미 인증한적 있는지 확인
		Optional<Joins> joinsByJoinsId = joinsRepository.findByJoinsId(joinsId);
		if(joinsByJoinsId.isPresent()) {
			log.error("MypageService > sendEmailToJoins - 이미 인증에 사용된 아이디 (joinsId : {}, userSeq : {})", joinsId, userSeq);
			throw new BusinessException(ErrorCode.ALREADY_USED, "이미 인증에 사용된 아이디입니다.");
		}
		
		// 3. 이메일 전송 
		String randomCode = mailUtil.createRandomCode();
		log.info("MypageService > sendEmailToJoins - 생성된 랜덤 코드 : {}, userSeq : {}", randomCode, userSeq);
		try {
			mailUtil.sendEmailToJoins(joinsId, randomCode);
		} catch (MessagingException e) {
			log.error("MypageService > sendEmailToJoins - 이메일 전송 실패 (userSeq : {}, joinsId : {})", userSeq, joinsId);
			throw new BusinessException(ErrorCode.FAILED_TO_SEND_EMAIL, "이메일 전송에 실패하였습니다.");
		}
		
		// 4. 인증번호 레디스에 저장 
		redisUtil.setDataWithExpire("joins-" + joinsId, randomCode, validationExpiration);
		return true;
	}
}
