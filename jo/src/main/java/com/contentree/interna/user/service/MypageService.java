package com.contentree.interna.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MypageService {

	private final UserRepository userRepository;
	
	//회원 정보 가져오기
	public Optional<User> getUserDetail(Long userSeq) {
		
		Optional<User> user = userRepository.findById(userSeq);
		return user;
	}
	
}
