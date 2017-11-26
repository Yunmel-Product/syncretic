package com.yunmel.extreme.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yunmel.extreme.annotation.Autowired;
import com.yunmel.extreme.annotation.Controller;
import com.yunmel.extreme.annotation.Service;
import com.yunmel.extreme.util.PackUtils;

public class AnnotationDriven {

	private static final Logger LOG = LoggerFactory.getLogger(AnnotationDriven.class);

	public static void annotationDriven(String packName) throws Exception {
		// 注入Service
		List<Class<?>> classSaveServicePaths = PackUtils.getClassListByAnnotation(packName, Service.class);
		saveServiceBean(classSaveServicePaths);
		
		//注入Contrller
		List<Class<?>> classSaveContrllerPaths = PackUtils.getClassListByAnnotation(packName, Controller.class);
		saveControllerBean(classSaveContrllerPaths);
		
		// 注入Autowired
		List<Class<?>> classInjectPaths = PackUtils.getClassListByAnnotation(packName, Autowired.class);
		inject(classInjectPaths);
	}
	
	private static void saveControllerBean(List<Class<?>> classSavePaths) throws InstantiationException, IllegalAccessException {
		for (Class<?> classPath : classSavePaths) {
			try {
				Class<?> c = Class.forName(classPath.getName());
				Object o = c.newInstance();
				// 扫描的到的含有注解的类实例化后保存在池中
				BeanFactory.addBean(classPath.getName(), o);
				//添加mapping
				MappingFactory.addMapping(c,o);
			} catch (ClassNotFoundException e) {
				LOG.error("初始化service bean 异常.",e);
				e.printStackTrace();
			}
		}
	}
	
	private static void saveServiceBean(List<Class<?>> classSavePaths) throws InstantiationException, IllegalAccessException {
		for (Class<?> classPath : classSavePaths) {
			try {
				Class<?> c = Class.forName(classPath.getName());
				Object o = c.newInstance();
				// 扫描的到的含有注解的类实例化后保存在池中
				BeanFactory.addBean(classPath.getName(), o);
			} catch (ClassNotFoundException e) {
				LOG.error("初始化service bean 异常.",e);
				e.printStackTrace();
			}
		}
	}
	
	private static void inject(List<Class<?>> classInjectPaths) throws Exception {
		Object o = null;
		for (Class<?> classInjectPath : classInjectPaths) {

			Class<?> c = Class.forName(classInjectPath.getName());
			// 判断存放bean的池中是否存在该bean
			if (BeanFactory.containsBean(classInjectPath.getName())) {
				o = BeanFactory.getBean(classInjectPath.getName());
			} else {
				o = c.newInstance();
			}
			Field[] fields = c.getDeclaredFields();
			for (Field field : fields) {
				Annotation[] annotations = field.getAnnotations();
				for (Annotation annotation : annotations) {
					// 判断是否是通过类型注解注入
					if (annotation instanceof Autowired) {
						Class<?> classField = field.getType();
						Object clazz = BeanFactory.getBean(classField.getName());
						field.setAccessible(true);
						field.set(o, clazz);
						BeanFactory.addBean(classInjectPath.getName(), o);
					}
				}
			}
		}
	}
}