package com.yunmel.web.exception;

public class NumberConverException extends WebException {
	private static final long serialVersionUID = 1L;

	public NumberConverException() {
        super();    
    }
    public NumberConverException(String message) {
        super(message);   
    }

    public NumberConverException(String message, Throwable cause) {
        super(message, cause);    
    }

    public NumberConverException(Throwable cause) {
        super(cause);    
    }

    protected NumberConverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);    
    }
}
