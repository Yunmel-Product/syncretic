package com.yunmel.extreme.web.dispatcher;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yunmel.extreme.util.StringUtils;

/**
 * 请求参数
 * 
 * @author xu
 *
 */
public class Parameter {
	// 请求的相对地址
	private String path;
	// 请求的地址
	private String requestURL;
	// 后缀
	private String extension;
	// 方法
	private String method;
	//
	private String contextPath;
	// 字符串参数
	private Map<String, String> params = new HtmlMap();
	// 文件参数
	// private Map<String,List<FileItem>> paramFile = new HashMap<>();

	private HttpServletRequest request;

	private HttpServletResponse response;

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	// public Map<String, List<FileItem>> getParamFile() {
	// return paramFile;
	// }
	//
	// public void setParamFile(Map<String, List<FileItem>> paramFile) {
	// this.paramFile = paramFile;
	// }

	public String getPath() {
		if(StringUtils.isBlank(path)){
			path = "/";
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
		this.contextPath = request.getServletContext().getContextPath();
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	private class HtmlMap extends HashMap<String, String> {
		private static final long serialVersionUID = 8173886449165163232L;

		@Override
		public String put(String key, String value) {
			// if(value!=null && ConfigUtils.getRequestEscape()) {
			// value = StringEscapeUtils.escapeHtml4(value);
			// }
			return super.put(key, value);
		}
	}

	@Override
	public String toString() {
		return "Parameter [path=" + path + ", requestURL=" + requestURL + ", extension=" + extension + ", method="
				+ method + ", paramString=" + params + "]";
	}

	public Object getParam(String name, Class<?> classes) {
		String value = params.get(name);
		if (String.class.isAssignableFrom(classes)) {
			return value;
		} else if (int.class.isAssignableFrom(classes) || Integer.class.isAssignableFrom(classes)) {
			return Integer.parseInt(value);
		} else if (long.class.isAssignableFrom(classes) || Long.class.isAssignableFrom(classes)) {
			return Long.parseLong(value);
		} else if (float.class.isAssignableFrom(classes) || Float.class.isAssignableFrom(classes)) {
			return Float.parseFloat(value);
		} else if (double.class.isAssignableFrom(classes) || Double.class.isAssignableFrom(classes)) {
			return Double.parseDouble(value);
		}
		return value;
	}

}
