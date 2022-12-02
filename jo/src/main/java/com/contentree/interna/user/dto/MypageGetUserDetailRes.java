package com.contentree.interna.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MypageGetUserDetailRes {
	String userName;
	String userEmail;
	String userBirth;
	String userCreateAt;
	String userGrade;
	Boolean userRole;
}
