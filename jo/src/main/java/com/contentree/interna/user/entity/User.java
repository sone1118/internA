package com.contentree.interna.user.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String userId;

	private String userName;

	private String userEmail;

	private String userPhone;

	private LocalDateTime userBirth;
	private Long userKakaoId;
	private String userRole;
	private String userGrade;
	private boolean userAgreeMarketing;
	private boolean userAgreeSns;

}
