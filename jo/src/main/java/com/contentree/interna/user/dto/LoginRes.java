package com.contentree.interna.user.dto;

import com.contentree.interna.global.model.BaseResponseBody;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "UserLoginRes", description = "로그인시 반환되는 값")
public class LoginRes extends BaseResponseBody {
	@ApiModelProperty(value = "accessToken")
	String accessToken;

	public static LoginRes of(Integer statusCode, String message, String accessToken) {
		LoginRes res = new LoginRes();
		res.setStatusCode(statusCode);
		res.setMessage(message);
		res.setAccessToken(accessToken);

		return res;
	}
}
