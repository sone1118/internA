package com.contentree.interna.user.service;

import java.util.Calendar;

import org.hibernate.hql.internal.ast.tree.BooleanLiteralNode;
import org.springframework.stereotype.Service;

import com.contentree.interna.user.dto.HomeGetUserDetailRes;
import com.contentree.interna.user.dto.MypageGetUserDetailRes;
import com.contentree.interna.user.entity.User;
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

	private final UserRepository userRepository;
		
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
	
	private String getGradeString(String grade) {
		if(grade == "GOLD") return "G";
		if(grade == "SILVER") return "S";
		return "B";		
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
