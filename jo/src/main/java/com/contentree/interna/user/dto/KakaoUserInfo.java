package com.contentree.interna.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KakaoUserInfo {
	Long id;
	String email;
	String nickname;
}
