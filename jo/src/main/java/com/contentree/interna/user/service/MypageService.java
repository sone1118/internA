package com.contentree.interna.user.service;

import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.contentree.interna.global.model.BusinessException;
import com.contentree.interna.global.model.ErrorCode;
import com.contentree.interna.global.util.CookieUtil;
import com.contentree.interna.global.util.JwtTokenUtil;
import com.contentree.interna.global.util.MailUtil;
import com.contentree.interna.global.util.RedisUtil;
import com.contentree.interna.user.controller.MypageContoller;
import com.contentree.interna.user.entity.Joins;
import com.contentree.interna.user.repository.JoinsRepository;
import com.contentree.interna.user.repository.UserRepository;

import groovyjarjarantlr4.v4.parse.ANTLRParser.id_return;
import groovyjarjarantlr4.v4.parse.ANTLRParser.throwsSpec_return;
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
	
	public int sendEmailToJoins(Long userSeq, String joinsId) {
		log.info("MypageService > sendEmailToJoins - 호출 (userSeq : {})", userSeq);
		
		// 1. 이미 임직원 인증한 회원인지 확인
		Optional<Joins> joinsById = joinsRepository.findById(userSeq);
		if(joinsById.isPresent()) {
			log.error("MypageService > sendEmailToJoins - 이미 인증된 사용자입니다. (joinsId : {}, userSeq : {})", joinsId, userSeq);
			return 1;
		}
		
		// 2. 해당 아이디로 이미 인증한적 있는지 확인
		Optional<Joins> joinsByJoinsId = joinsRepository.findByJoinsId(joinsId);
		if(joinsByJoinsId.isPresent()) {
			log.error("MypageService > sendEmailToJoins - 이미 인증에 사용된 아이디입니다. (joinsId : {}, userSeq : {})", joinsId, userSeq);
			return 2;
		}
		
		// 3. 이메일 전송 
		String randomCode = mailUtil.createRandomCode();
		try {
			mailUtil.sendEmailToJoins(joinsId, randomCode);
		} catch (MessagingException e) {
			log.error("MypageService > sendEmailToJoins - 이메일 전송 실패 (userSeq : {}, joinsId : {})", userSeq, joinsId);
			return 3;
		}
		
		// 4. 인증번호 레디스에 저장 
		redisUtil.setDataWithExpire("joins-" + joinsId, randomCode, validationExpiration);
		return 4;
	}
	
	// [ 김지슬 ] 임직원 인증 코드 검증 
	public boolean checkJoinsEmailCode(Long userSeq, String certificationCode) {
		String userSeqString = userSeq.toString();
		log.info("MypageService > checkJoinsEmailCode - 호출 (userSeq : {}, certificationCode : {})", userSeqString, certificationCode);
		
		// 1. 유저 seq로 저장된 인증번호 가져오기 및 대조 
		String certKey = "cert-" + userSeqString;
		String originCertificationCode = redisUtil.getData(certKey);
		log.info("MypageService > checkJoinsEmailCode - 저장된 인증번호 가져오기 (userSeq : {}, originCertificationCode : {})", userSeqString, originCertificationCode);
		// 1-1. 해당 유저로 저장된 인증코드가 없거나 (시간 초과), 
		if (originCertificationCode == null) {
			log.error("MypageService > checkJoinsEmailCode - 인증 시간 만료 (userSeq : {}, certificationCode : {})", userSeqString, certificationCode);
			throw new BusinessException(ErrorCode.TIME_OUT, "인증 시간이 만료되었습니다.");
		// 1-2. 입력 받은 코드와 일치하지 않는 경우
		} else if (!originCertificationCode.equals(certificationCode)) {
			log.error("MypageService > checkJoinsEmailCode - 인증 코드 불일치 (userSeq : {}, originCertCode : {}, userInputCertCode : {})", userSeqString, originCertificationCode, certificationCode);
			throw new BusinessException(ErrorCode.WRONG_CERT_CODE, "인증 코드가 일치하지 않습니다.");
		}
		
		// 2. userSeq로 저장된 인증번호 데이터 삭제 
		redisUtil.deleteData(certKey);
		
		// 3. 저장된 joinsId 가져와 저장하기 
		String joinsKey = "joins-" + userSeqString;
		String joinsId = redisUtil.getData(joinsKey);
		log.info("MypageService > checkJoinsEmailCode - 저장된 joins id 가져오기 (userSeq : {}, joinsId : {})", userSeqString, joinsId);
		Joins joins = Joins.builder().userSeq(userSeq).joinsId(joinsId).build();
		joinsRepository.save(joins);
		
		// 4. userSeq로 저장된 joins id 데이터 삭제 
		redisUtil.deleteData(joinsKey);
		
		return true;
	}

	

}
