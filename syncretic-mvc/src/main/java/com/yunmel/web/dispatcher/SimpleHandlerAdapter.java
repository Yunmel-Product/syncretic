package com.yunmel.web.dispatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.yunmel.util.StringUtils;
import com.yunmel.util.scan.MethodParamNamesScaner;
import com.yunmel.web.ModelAndView;
import com.yunmel.web.annotation.ModelAttribute;
import com.yunmel.web.annotation.PathVariable;
import com.yunmel.web.annotation.ResponseBody;
import com.yunmel.web.bean.BeanFactory;
import com.yunmel.web.bean.MappingFactory;
import com.yunmel.web.exception.ControllerNotFoundException;
import com.yunmel.web.exception.MethodNotAllowedException;
import com.yunmel.web.meta.MethodObj;
import com.yunmel.web.meta.ModelMap;
import com.yunmel.web.meta.RespType;

public class SimpleHandlerAdapter implements HandlerAdapter {
	private static Logger LOG = LoggerFactory.getLogger(SimpleHandlerAdapter.class);
	
	public SimpleHandlerAdapter() {
		
	}

	@Override
	public ModelAndView process(Parameter parameter) throws Exception {
		if (parameter.getMethod().equals("HEAD")) {
			return new ModelAndView();
		}
		//判断是否映射了该URL
		String url = MappingFactory.getUrlPath(parameter.getPath());
		if(StringUtils.isBlank(url)){
			LOG.error("找不到映射关系,参数：{}", parameter.toString());
			throw new ControllerNotFoundException("找不到映射关系。");
		}
		
		if(!MappingFactory.allowMethod(url, parameter.getMethod())){
			LOG.error("找不到映射关系,参数：{}", parameter.toString());
			throw new MethodNotAllowedException("方法不允许");
		}
		MethodObj mObj = MappingFactory.get(url);
		Object instance = BeanFactory.getBean(mObj.getClassName());
		Method requestMethod = mObj.getMethod();
		Class<?>[] argTypes = requestMethod.getParameterTypes();
		Annotation[][] paramAnns = requestMethod.getParameterAnnotations();
		Object data = null;
		ModelMap model = null;
		if (argTypes.length == 0) {
			data = requestMethod.invoke(instance);
		} else {
			long start = System.currentTimeMillis();
			Object[] args = new Object[argTypes.length];
			List<String> params = MethodParamNamesScaner.getParamNames(requestMethod);
			for (int i = 0; i < argTypes.length; i++) {
				if (paramAnns[i] != null && paramAnns[i].length > 0) {
					for (Annotation ann : paramAnns[i]) {
						if (ann.annotationType().equals(PathVariable.class)) {
							PathVariable rp = (PathVariable) ann;
							String arg = mObj.getPathMap().get(rp.value());
							if(String.class.isAssignableFrom(argTypes[i])){
								args[i] = arg;
							}else if (Integer.class.isAssignableFrom(argTypes[i])) {
								args[i] = Integer.valueOf(arg);
							}else if (Long.class.isAssignableFrom(argTypes[i])) {
								args[i] = Long.valueOf(arg);
							}else{
								LOG.error("PathVariable 参数[{}]绑定错误.",rp.value());
							}
							break;
						} else if (ann.annotationType().equals(ModelAttribute.class)) {
							String json = JSON.toJSONString(parameter.getParams());
							args[i] = JSON.parseObject(json, argTypes[i]);
							break;
						}
					}
				} else if (ModelMap.class.isAssignableFrom(argTypes[i])) {
					model = new ModelMap();
					args[i] = model;
				} else {
					args[i] = parameter.getParam(params.get(i), argTypes[i]);
				}
			}
			LOG.info("{} invoke pre cost [{}] 毫秒",requestMethod.getName(),(System.currentTimeMillis() - start));
			start = System.currentTimeMillis();
			data = requestMethod.invoke(instance, args);
			LOG.info("{} invoke cost [{}] 毫秒",requestMethod.getName(),(System.currentTimeMillis() - start));
		}
		if (data == null) {
			if (requestMethod.getReturnType().getName().equals("void")) {
				return null;
			}
		}
		ModelAndView view = null;
		if (data instanceof ModelAndView) {
			view = (ModelAndView) data;
		} else {
			view = new ModelAndView();
		}
		ResponseBody resAnno = requestMethod.getAnnotation(ResponseBody.class);
		if (null == resAnno) {
			view.setRt(RespType.PAGE);
		} else {
			view.setRt(RespType.JSON);
		}
		view.setView(data);
		if (model != null) {
			view.add(model);
		}
		return view;
	}

}
