package com.contentree.interna.global.common.response;

import com.contentree.interna.user.entity.Grade;
import com.contentree.interna.user.entity.Role;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class TestResponse2 {
	String userName;
	Grade userGrade;
	Role userRole;
	String userBirth;
}
