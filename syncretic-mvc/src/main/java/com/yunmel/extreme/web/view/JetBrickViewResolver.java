package com.yunmel.extreme.web.view;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yunmel.extreme.web.ViewResolver;

import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;
import jetbrick.template.web.JetWebContext;
import jetbrick.template.web.JetWebEngine;

public class JetBrickViewResolver implements ViewResolver {

	private JetEngine jetEngine;
	private String suffix;
	private String root = "/";

	@Override
	public void init(ServletContext context) throws ServletException {
		this.jetEngine = JetWebEngine.getEngine();
		this.suffix = jetEngine.getConfig().getTemplateSuffix();
	}

	@Override
	public void resolveView(String view, Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JetWebContext context = new JetWebContext(request, response, model);
		if (!view.endsWith(suffix)) {
			view = view.concat(".").concat(suffix);
		}
		view = root.concat(view);
		JetTemplate template = jetEngine.getTemplate(view);
		template.render(context, response.getOutputStream());
	}

}
