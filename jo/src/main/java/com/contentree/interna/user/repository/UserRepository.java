package com.contentree.interna.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contentree.interna.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	// select * from user_master where kakao_email = ?
	public User findByUserPhone(String userPhone);

}
