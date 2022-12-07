package com.contentree.interna.global.model;

/**
 * 
 * @author 김지슬
 *
 */
public class BusinessException extends RuntimeException {
	private ErrorCode errorCode;
    private String message;

    public BusinessException(ErrorCode errorCode, String message) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getMessage() {
        return message;
    }
}
