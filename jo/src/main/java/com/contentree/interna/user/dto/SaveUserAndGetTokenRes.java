package com.contentree.interna.user.dto;

import com.contentree.interna.user.entity.Grade;
import com.contentree.interna.user.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class SaveUserAndGetTokenRes {
	String refreshToken;
	String accessToken;
	String userName;
	Grade userGrade;
	Role userRole;
}
