package com.yunmel.web.exception;

public class WebException extends RuntimeException{
	private static final long serialVersionUID = 4385649106022630334L;

	public WebException() {
        super();
    }

    public WebException(String message) {
        super(message);
    }

    public WebException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebException(Throwable cause) {
        super(cause);
    }

    protected WebException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
