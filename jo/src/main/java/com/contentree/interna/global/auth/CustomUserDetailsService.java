package com.contentree.interna.global.auth;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService{
	
	private final UserRepository userRepository;
	
	// userSeq로 유저 데이터 가져오기 
	public User getUserDetail(Long userSeq) {
		Optional<User> user = userRepository.findById(userSeq);
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

}
