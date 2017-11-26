package com.yunmel.extreme.web.meta;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MethodObj {
	private Method method;
	private String className;
	private Map<String,String> pathMap = new HashMap<String,String>();
	
	public MethodObj(String className, Method method) {
		this.className = className;
		this.method = method;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Map<String, String> getPathMap() {
		return pathMap;
	}
	public void setPathMap(Map<String, String> pathMap) {
		this.pathMap = pathMap;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	
}
