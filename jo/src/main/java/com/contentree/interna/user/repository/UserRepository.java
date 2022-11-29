package com.contentree.interna.user.repository;

 import com.contentree.interna.user.entity.User; 
 import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
 @Repository
 public interface UserRepository extends JpaRepository<User, String>{
	    // JPA findBy 규칙
	    // select * from user_master where kakao_email = ?
	    public User findByUserEmail(String userEmail);
	    public User findByUserSeq(Long userSeq);
 }
 