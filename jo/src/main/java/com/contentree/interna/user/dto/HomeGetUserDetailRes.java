package com.contentree.interna.user.dto;

import lombok.Data;

@Data
public class HomeGetUserDetailRes {
	 String userName;
	 String userGrade;
	 Boolean userRole;
	 Boolean userBirth;
}