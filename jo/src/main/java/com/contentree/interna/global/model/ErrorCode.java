package com.contentree.interna.global.model;

/**
 * 
 * @author 김지슬
 *
 */
public enum ErrorCode {
	// JWT 
	BLACK_REFRESH(400, "AUTH_001", "사용할 수 없는 Refresh Token"),
	BLACK_ACCESS(400, "AUTH_002", "사용할 수 없는 Access Token"),
	CREATOR_ERROR(400, "AUTH_003", "Refresh Token을 만든 userSeq와 Access Token에 담긴 userSeq가 불일치합니다."),
	NONEXISTENT_USER(400, "AUTH_004", "존재하지 않는 사용자입니다."),
	
	// Mypage
	ALREADY_CERTIFIED(400, "MYPAGE_001", "이미 인증된 임직원."),
	ALREADY_USED(400, "MYPAGE_002", "이미 인증에 사용된 아이디"),
	FAILED_TO_SEND_EMAIL(400, "MYPAGE_003", "이메일 전송 실패");
	
	
 	private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
