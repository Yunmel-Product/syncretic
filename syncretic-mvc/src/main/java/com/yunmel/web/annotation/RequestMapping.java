package com.yunmel.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface RequestMapping {

	String[] value() default {};

	RequestMethod[] method() default {};

	String template() default "";

	boolean singleton() default false;
}
