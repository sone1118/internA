package com.contentree.interna.user.service;

import java.util.Calendar;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.contentree.interna.global.model.BusinessException;
import com.contentree.interna.global.model.ErrorCode;
import com.contentree.interna.global.util.MailUtil;
import com.contentree.interna.global.util.RedisUtil;
import com.contentree.interna.user.dto.HomeGetUserDetailRes;
import com.contentree.interna.user.dto.MypageGetUserDetailRes;
import com.contentree.interna.user.entity.Joins;
import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.repository.JoinsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 손혜진, 김지슬
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
		

	// [ 손혜진 ] 회원 정보 가져오기
	public HomeGetUserDetailRes getUserDetail(Long userSeq) {
		//유저 정보 가져오기
		log.info("service 요청이 들어온 userSeq " + userSeq);
		log.info("home의 데이터를 요청했습니다.");
		User user = userRepository.findById(userSeq).get();
			
		//home 화면에서 쓸 userDTO 설정
		HomeGetUserDetailRes userDetail = new HomeGetUserDetailRes();
		userDetail.setUserName(user.getUserName());
		userDetail.setUserBirth(todayIsBirthday(user.getUserBirth()));
		
		//getGradeString를 이용해서 grade를 변환해 주고 싶었는데 이것을 service에서 해도 되는지, util 함수로 따로빼서 가독성을 높이고 싶었다.
		String grade = getGradeString(user.getUserGrade().toString());
		userDetail.setUserGrade(grade);
		
		//role: joins일 경우에 true로
		userDetail.setUserRole((user.getUserRole().toString() == "ROLE_JOINS") ? true : false);
		
		return userDetail;
	}
	

	// [ 손혜진 ] 회원 정보를 *처리해서 가져오기
	public MypageGetUserDetailRes getUserDetailWithStar(Long userSeq) {
		//유저 정보 가져오기
		log.info("유저정보 가져오기: seq" + userSeq);
		log.info("mypage의 data를 요청했습니다.");
		User user = userRepository.findById(userSeq).get();
		
		//*처리해서 보내준다.
		MypageGetUserDetailRes userDetail = new MypageGetUserDetailRes();
		userDetail.setUserName(user.getUserName());
		
		//이메일 *처리
		String email = getHiddenEmail(user.getUserEmail());
		userDetail.setUserEmail(email);
		
		userDetail.setUserBirth("****.**.**");
		userDetail.setUserCreateAt("****.**.**");
		userDetail.setUserGrade(user.getUserGrade().name());
		userDetail.setUserRole((user.getUserRole().toString() == "ROLE_JOINS") ? true : false);
	
		return userDetail;
	}
	

	//util로 빼서 static으로 하면
	private String getGradeString(String grade) {
		if(grade == "GOLD") return "G";
		if(grade == "SILVER") return "S";
		return "B";	
		//"GOLD".equals(grade); true false로
	}
	
	private Boolean todayIsBirthday(Calendar birth) {
		Calendar today = Calendar.getInstance();
		if(today.get(Calendar.MONTH) != birth.get(Calendar.MONTH)) return false;
		if(today.get(Calendar.DATE) != birth.get(Calendar.DATE)) return false;
		return true;
	} 
	
	private String getHiddenEmail(String email) {
		String[] splitedEmail = email.split("@");
		String frontString = splitedEmail[0].replaceAll("(?<=.{1}).", "*");
		String backString = splitedEmail[1].replaceAll("(?<=.{2}).", "*");
		return (frontString + "@" + backString);
	}
}
