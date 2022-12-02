package com.contentree.interna.user.dto;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class HomeGetUserDetailRes {
	String userName;
	String userGrade;
	Boolean userRole;
	Boolean userBirth;
}
