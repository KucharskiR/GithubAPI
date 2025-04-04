package com.githubapi.githubapi.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class GithubApiException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final HttpStatus status;
    
    public GithubApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    
    public GithubApiException(String message, int statusCode) {
		super(message + " (Status code: " + statusCode + ")");
        this.status = HttpStatus.valueOf(statusCode);
    }
}
