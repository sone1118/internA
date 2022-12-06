package com.contentree.interna.user.entity;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "withdrawal_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WithdrawalUser {
	@Id
	@Column(name = "user_seq")
	private Long userSeq;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "user_email")
	private String userEmail;
	
	@Column(name = "user_phone")
	private String userPhone;
	
	@Column(name = "user_birth")
	@Temporal(TemporalType.DATE)
	private Calendar userBirth;
	
	@Column(name = "user_kakao_id")
	private Long userKakaoId; 
	
	@Column(name = "user_role")
	@Enumerated(EnumType.STRING)
	private Role userRole;
	
	@Column(name = "user_grade")
	@Enumerated(EnumType.STRING)
	private Grade userGrade;
	
	@Column(name = "user_agree_marketing")
	private boolean userAgreeMarketing;
	
	@Column(name = "user_agree_sns")
	private boolean userAgreeSns;
	
	@Column(name = "user_expiration_date")
	@Temporal(TemporalType.DATE)
	private Calendar userExpirationDate;
	
	@Column(name = "user_joins_id")
	private String userJoinsId;

	@Builder
	public WithdrawalUser(Long userSeq, String userName, String userEmail, String userPhone, Calendar userBirth,
			Long userKakaoId, Role userRole, Grade userGrade, boolean userAgreeMarketing, boolean userAgreeSns, 
			Calendar userExpirationDate, String userJoinsId) {
		this.userSeq = userSeq;
		this.userName = userName;
		this.userEmail = userEmail;
		this.userPhone = userPhone;
		this.userBirth = userBirth;
		this.userKakaoId = userKakaoId;
		this.userRole = userRole;
		this.userGrade = userGrade;
		this.userAgreeMarketing = userAgreeMarketing;
		this.userAgreeSns = userAgreeSns;
		this.userExpirationDate = userExpirationDate;
		this.userJoinsId = userJoinsId;
	}
}

