package com.yunmel.web.exception;

public class MethodNotAllowedException extends WebException {
	private static final long serialVersionUID = 1L;

	public MethodNotAllowedException() {
        super();    
    }
    public MethodNotAllowedException(String message) {
        super(message);   
    }

    public MethodNotAllowedException(String message, Throwable cause) {
        super(message, cause);    
    }

    public MethodNotAllowedException(Throwable cause) {
        super(cause);    
    }

    protected MethodNotAllowedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);    
    }
}
