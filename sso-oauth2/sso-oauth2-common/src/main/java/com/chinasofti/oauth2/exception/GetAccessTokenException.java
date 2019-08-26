package com.chinasofti.oauth2.exception;

/**
 * Created by yangkai on 15/8/3.
 */
public class GetAccessTokenException extends RuntimeException {

    public GetAccessTokenException(Throwable cause) {
        super(cause);
    }

	public GetAccessTokenException(String msg) {
		super(msg);
	}
}
