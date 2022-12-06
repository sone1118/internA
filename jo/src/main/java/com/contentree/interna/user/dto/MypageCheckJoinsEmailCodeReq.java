package com.contentree.interna.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 
 * @author 김지슬
 *
 */
@Getter
public class MypageCheckJoinsEmailCodeReq {
	@Schema(description = "이메일로 전달받은 8자리 코드", example = "EK3F1K0J")
	String certificationCode;
}
