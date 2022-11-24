package com.contentree.interna.user.oauth2.exception;

/*
 * 자원이 존재하지 않을 때 예외 처리
 */
public class OAuth2NotFoundException extends RuntimeException {
	public OAuth2NotFoundException(String message) {
		super(message);
	}
}
