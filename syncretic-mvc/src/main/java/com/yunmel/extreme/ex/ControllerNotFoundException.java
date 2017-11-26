package com.yunmel.extreme.ex;

public class ControllerNotFoundException extends WebException {
	private static final long serialVersionUID = 1L;

	public ControllerNotFoundException() {
        super();    
    }
    public ControllerNotFoundException(String message) {
        super(message);   
    }

    public ControllerNotFoundException(String message, Throwable cause) {
        super(message, cause);    
    }

    public ControllerNotFoundException(Throwable cause) {
        super(cause);    
    }

    protected ControllerNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);    
    }
}
