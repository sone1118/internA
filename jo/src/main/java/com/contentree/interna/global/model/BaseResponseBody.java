package com.contentree.interna.global.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 김지슬
 *
 */
@Getter
@Setter
@Schema(description = "Base Response body")
public class BaseResponseBody {
	@Schema(description = "응답 코드", example = "200")
	Integer statusCode = null;
	
	@Schema(description = "응답 메시지", example = "정상")
	String message = null;
	
	public BaseResponseBody() {}
	
	public BaseResponseBody(Integer statusCode){
		this.statusCode = statusCode;
	}
	
	public BaseResponseBody(Integer statusCode, String message){
		this.statusCode = statusCode;
		this.message = message;
	}
	
	public static BaseResponseBody of(Integer statusCode, String message) {
		BaseResponseBody body = new BaseResponseBody();
		body.message = message;
		body.statusCode = statusCode;
		return body;
	}
}