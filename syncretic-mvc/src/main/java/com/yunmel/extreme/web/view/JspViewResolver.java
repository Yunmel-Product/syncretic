package com.yunmel.extreme.web.view;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yunmel.extreme.web.ViewResolver;

public class JspViewResolver implements ViewResolver {
	private String root = "/";
	/**
	 * Init JspViewResolver.
	 */
	public void init(ServletContext context) throws ServletException {
		Object rootObj = context.getAttribute("root");
		if (null != rootObj) {
			root = (String) rootObj;
			if (!root.endsWith("/"))
				root = root.concat("/");
		}
	}

	/**
	 * Render view using JSP.
	 */
	public void resolveView(String view, Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (model != null) {
			Set<String> keys = model.keySet();
			for (String key : keys) {
				request.setAttribute(key, model.get(key));
			}
		}
		if (!view.endsWith(".jsp")) {
			view += ".jsp";
		}
		view = root.concat(view);
		request.getRequestDispatcher(view).forward(request, response);
	}

}
