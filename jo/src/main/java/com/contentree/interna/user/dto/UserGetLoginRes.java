package com.contentree.interna.user.dto;

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
