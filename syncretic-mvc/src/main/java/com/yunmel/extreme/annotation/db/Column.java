package com.yunmel.extreme.annotation.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	public String length() default "255";

	public String name();

	public boolean nullable() default false;

	public boolean unique() default false;

	public String type() default "VARCHAR";
}