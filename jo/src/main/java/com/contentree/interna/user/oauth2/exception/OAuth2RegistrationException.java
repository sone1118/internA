package com.contentree.interna.user.oauth2.exception;

/*
 * 가입 실패시 예외 처리
 */
public class OAuth2RegistrationException extends RuntimeException {
	public OAuth2RegistrationException(String message) {
		super(message);
	}
}
