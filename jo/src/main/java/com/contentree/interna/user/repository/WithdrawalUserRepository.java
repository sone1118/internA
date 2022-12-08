package com.contentree.interna.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contentree.interna.user.entity.WithdrawalUser;

public interface WithdrawalUserRepository extends JpaRepository<WithdrawalUser, Long>{

}
