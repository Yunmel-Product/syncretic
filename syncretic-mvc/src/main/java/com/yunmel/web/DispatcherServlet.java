package com.yunmel.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yunmel.web.bean.AnnotationDriven;
import com.yunmel.web.dispatcher.Parameter;
import com.yunmel.web.dispatcher.ServiceHandler;
import com.yunmel.web.exception.ControllerNotFoundException;
import com.yunmel.web.exception.MethodNotAllowedException;
import com.yunmel.web.meta.RespType;
import com.yunmel.web.view.JspViewResolver;

public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 3933217248767534032L;
	private static final Logger LOG = LoggerFactory.getLogger(DispatcherServlet.class);

	private ServiceHandler sh;
	private ViewResolver viewResolver;
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		long start = System.currentTimeMillis();
		try {
			String packageName = config.getInitParameter("package");
			AnnotationDriven.annotationDriven(packageName);
		} catch (Exception e) {
			LOG.error("annotation 注入失败...", e);
		}
		this.sh = ServiceHandler.initialize();
		this.viewResolver = new JspViewResolver();
		
		long cos = System.currentTimeMillis() - start;
		LOG.info("服务器启动成功，功花费[{}]ms.", cos);
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		LOG.info("service");
		Parameter parameter = null;
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String url = request.getRequestURI();
		try {
			long start = System.currentTimeMillis();
			parameter = sh.initializeParameter(request, response);
			if (parameter.getMethod().equals("OPTIONS")) {
				response.setHeader("Access-Control-Allow-Origin", "*");
				response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
				response.setHeader("Access-Control-Allow-Headers", "X-PINGOTHER");
				return;
			}

			HttpSession session = request.getSession();
			ServletContext context = session.getServletContext();
			WebContext.setActionContext(request, response, session, context);
			ModelAndView mv = sh.processRequest(parameter);
			if (mv != null)
				render(mv, request, response);
			LOG.info("执行[{}],花费[{}]毫秒.", url, (System.currentTimeMillis() - start));
		} catch (Exception e) {
			processException(e, parameter.getRequestURL(), response);
		}
	}

	private void processException(Exception e, String uri, HttpServletResponse resp) throws IOException {
		e.printStackTrace();
		if (e instanceof InvocationTargetException) {
			Throwable throwable = e.getCause();
			if (throwable instanceof NumberFormatException) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			} else if (e instanceof MethodNotAllowedException) {
				resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			} else if (e instanceof ControllerNotFoundException) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		} else {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	// render view:
	private void render(ModelAndView mv, HttpServletRequest reqest, HttpServletResponse response)
			throws ServletException, IOException {
		Object view = mv.getView();
		if (mv.getRt().equals(RespType.JSON)) {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print(JSONObject.toJSONString(view));
			out.flush();
			out.close();
		} else {
			if (view.getClass().equals(String.class)) {
				String temp = view.toString();
				if (temp.startsWith("redirect:")) {
					String redirect = temp.substring("redirect:".length());
					if (LOG.isDebugEnabled())
						LOG.debug("Send a redirect to: " + redirect);
					response.sendRedirect(redirect);
					return;
				}
				Map<String, Object> model = mv.getModel();
				if (LOG.isDebugEnabled())
					LOG.debug("Render view: " + view);
				if (viewResolver != null)
					viewResolver.resolveView(temp, model, reqest, response);
			} else {
				PrintWriter writer = response.getWriter();
				writer.write(view.toString());
				writer.flush();
				writer.close();
			}
		}
	}
}
