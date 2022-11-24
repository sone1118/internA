package com.contentree.interna.user.oauth2;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OAuth2Attributes {
	private final Map<String, Object> attributes;
	private final String nameAttributeKey;
	private final String oauthId;
	private final String nickname;
	private final String email;
	private final String birthday;
	private final Provider provider;

	@Builder
	public OAuth2Attributes(Map<String, Object> attributes, String nameAttributeKey, String oauthId, String nickname,
			String email, String birthday, Provider provider) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.oauthId = oauthId;
		this.nickname = nickname;
		this.email = email;
		this.birthday = birthday;
		this.provider = provider;
	}

}
