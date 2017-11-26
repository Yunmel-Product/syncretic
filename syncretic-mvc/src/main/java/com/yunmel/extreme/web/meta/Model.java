package com.yunmel.extreme.web.meta;

import java.util.LinkedHashMap;

public class Model extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = -5324089892920333227L;

	public void add(String key, Object data) {
		this.put(key, data);
	}
}
