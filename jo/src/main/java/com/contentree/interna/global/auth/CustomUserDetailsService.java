package com.contentree.interna.global.auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.repository.UserRepository;

@Service
public class CustomUserDetailsService {

	@Autowired
	private UserRepository userRepository;

	// userSeq로 유저 데이터 가져오기
	public User getUserDetail(Long userSeq) {
		Optional<User> user = userRepository.findById(userSeq);
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

}
