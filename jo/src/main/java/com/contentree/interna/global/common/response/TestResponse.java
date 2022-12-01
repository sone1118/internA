package com.contentree.interna.global.common.response;

import java.util.Calendar;

import com.contentree.interna.user.entity.Grade;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class TestResponse {
	String userName;
	String userEmail;
	Calendar userBirth;
	Calendar userCreateAt;
	Grade userGrade;
}
