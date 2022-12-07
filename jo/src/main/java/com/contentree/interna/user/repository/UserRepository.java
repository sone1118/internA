package com.contentree.interna.user.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.contentree.interna.user.entity.Role;
import com.contentree.interna.user.entity.User;

@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
	// select * from user_master where kakao_email = ?
	User findByUserPhone(String userPhone);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE User user SET user.userRole = :userRole WHERE user.userSeq = :userSeq")
	int updateUserRole(Long userSeq, Role userRole);
	
}