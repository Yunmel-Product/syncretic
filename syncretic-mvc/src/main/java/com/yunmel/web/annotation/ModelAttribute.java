package com.yunmel.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelAttribute {

	/**
	 * Alias for {@link #name}.
	 */
	String value() default "";

	/**
	 * Allows declaring data binding disabled directly on an {@code @ModelAttribute}
	 * method parameter or on the attribute returned from an {@code @ModelAttribute}
	 * method, both of which would prevent data binding for that attribute.
	 * <p>By default this is set to {@code true} in which case data binding applies.
	 * Set this to {@code false} to disable data binding.
	 * @since 4.3
	 */
	boolean binding() default true;

}
