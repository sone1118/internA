package com.contentree.interna.global.model;

/**
 * 
 * @author 김지슬
 *
 */
public enum ErrorCode {
	BLACK_REFRESH(403, "AUTH_001", "사용할 수 없는 Refresh Token"),
	BLACK_ACCESS(403, "AUTH_002", "사용할 수 없는 Access Token"),
	CREATOR_ERROR(403, "AUTH_003", "Refresh Token을 만든 userSeq와 Access Token에 담긴 userSeq가 불일치합니다."),
	NONEXISTENT_USER(403, "AUTH_004", "존재하지 않는 사용자입니다.");
	
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
