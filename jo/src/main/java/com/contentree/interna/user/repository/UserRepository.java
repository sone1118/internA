package com.contentree.interna.user.repository;

import com.contentree.interna.user.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

//	 Optional<User> findBySeq(Long userSeq);
//	 Optional<User> findByKakaoId(Long userKakaoId);
	 
 }
 