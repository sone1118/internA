package com.contentree.interna.user.service;

import java.util.Calendar;

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
		User user = userRepository.findById(userSeq).get();
		
		//birth: 오늘날짜와 비교해서 같으면 true
		Calendar today = Calendar.getInstance();
		Calendar birth = user.getUserBirth(); 
		Boolean userBirth = today.get(Calendar.MONTH) == birth.get(Calendar.MONTH);
		userBirth = userBirth && (today.get(Calendar.DATE) == birth.get(Calendar.DATE));
		
		HomeGetUserDetailRes userDetail = new HomeGetUserDetailRes();
		userDetail.setUserName(user.getUserName());
		userDetail.setUserGrade(user.getUserGrade().toString());
		//role: joins일 경우에 true로
		userDetail.setUserRole((user.getUserRole().toString() == "ROLE_JOINS") ? true : false);
		userDetail.setUserBirth(userBirth);
		
		return userDetail;
	}
	
	// [ 손혜진 ] 회원 정보를 *처리해서 가져오기
	public MypageGetUserDetailRes getUserDetailWithStar(Long userSeq) {
		//유저 정보 가져오기
		log.info("유저정보 가져오기: seq" + userSeq);
		User user = userRepository.findById(userSeq).get();
		
		//*처리해서 보내준다.
		MypageGetUserDetailRes userDetail = new MypageGetUserDetailRes();
		userDetail.setUserName(user.getUserName());
		//이메일 *처리
		String[] email = user.getUserEmail().split("@");
		String frontString = email[0].replaceAll("(?<=.{1}).", "*");
		String backString = email[1].replaceAll("(?<=.{2}).", "*");
		userDetail.setUserEmail(frontString + "@" + backString);
		
		userDetail.setUserBirth("****.**.**");
		userDetail.setUserCreateAt("****.**.**");
		userDetail.setUserGrade(user.getUserGrade().name());
		userDetail.setUserRole((user.getUserRole().toString() == "ROLE_JOINS") ? true : false);
	
		return userDetail;
	}
	
}
