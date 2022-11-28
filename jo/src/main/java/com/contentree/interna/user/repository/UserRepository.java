package com.contentree.interna.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.contentree.interna.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{

}
