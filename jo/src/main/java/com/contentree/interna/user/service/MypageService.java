package com.contentree.interna.user.service;

import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.contentree.interna.global.model.BusinessException;
import com.contentree.interna.global.model.ErrorCode;
import com.contentree.interna.global.util.MailUtil;
import com.contentree.interna.global.util.MaskingUtil;
import com.contentree.interna.global.util.RedisUtil;
import com.contentree.interna.user.dto.HomeGetUserDetailRes;
import com.contentree.interna.user.dto.MypageGetUserDetailRes;
import com.contentree.interna.user.entity.Joins;
import com.contentree.interna.user.entity.Role;
import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.repository.JoinsRepository;
import com.contentree.interna.user.repository.UserRepository;

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
	
	private final UserRepository userRepository;
	private final JoinsRepository joinsRepository;
	
	private final MailUtil mailUtil;
	private final RedisUtil redisUtil;
	private final MaskingUtil maskingUtil;
	
	// [ 김지슬 ] 임직원 인증 인증번호 전송
	public void sendEmailToJoins(Long userSeq, String joinsId) {
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
		
		// 4. 인증번호 및 joins id Redis에 저장 
		redisUtil.setDataWithExpire("cert-" + userSeq, randomCode, validationExpiration);
		redisUtil.setDataWithExpire("joins-" + userSeq, joinsId, validationExpiration);
	}
	
	
	// [ 김지슬 ] 임직원 인증 코드 검증 
	public void checkJoinsEmailCode(Long userSeq, String certificationCode) {
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
		
		// 5. user Role Joins로 변경
		userRepository.updateUserRole(userSeq, Role.ROLE_JOINS);
		log.info("MypageService > checkJoinsEmailCode - user role 업데이트 성공 (userSeq : {}, 변경된 userRole : {})", userSeq, "ROLE_JOINS");
	}

	
	// [ 손혜진 ] 회원 정보 가져오기
	public HomeGetUserDetailRes getUserDetail(Long userSeq) {
		//유저 정보 가져오기
		log.info("service 요청이 들어온 userSeq " + userSeq);
		log.info("home의 데이터를 요청했습니다.");
		User user = userRepository.findById(userSeq).get();
			
		if(user == null) throw new NullPointerException();
		
		//home 화면에서 쓸 userDTO 설정
		HomeGetUserDetailRes userDetail = new HomeGetUserDetailRes();
		userDetail.setUserName(user.getUserName());
		userDetail.setUserBirth(maskingUtil.todayIsBirthday(user.getUserBirth()));
		
		//getGradeString를 이용해서 grade를 변환해 주고 싶었는데 이것을 service에서 해도 되는지, util 함수로 따로빼서 가독성을 높이고 싶었다.
		String grade = maskingUtil.getGradeString(user.getUserGrade().toString());
		userDetail.setUserGrade(grade);;
		
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
		if(user == null) throw new NullPointerException();
		//*처리해서 보내준다.
		MypageGetUserDetailRes userDetail = new MypageGetUserDetailRes();
		userDetail.setUserName(user.getUserName());
		
		//이메일 *처리
		String email = maskingUtil.getHiddenEmail(user.getUserEmail());
		userDetail.setUserEmail(email);
		
		userDetail.setUserBirth("****.**.**");
		userDetail.setUserCreateAt("****.**.**");
		userDetail.setUserGrade(user.getUserGrade().name());
		userDetail.setUserRole((user.getUserRole().toString() == "ROLE_JOINS") ? true : false);
	
		return userDetail;
	}
}