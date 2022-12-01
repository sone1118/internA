package com.contentree.interna.global.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class MypageSendEmailToJoinsReq {
	@Schema(description = "메일 인증 받을 조인스 아이디", example = "joongang123")
	String joinsId;
}
