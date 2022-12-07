package com.contentree.interna.user.dto;

import com.contentree.interna.user.entity.Grade;
import com.contentree.interna.user.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserGetLoginRes {
	String userName;
	String userGrade;
	Boolean userRole;
	Boolean userBirth;
}
