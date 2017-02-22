package com.yunmel.test;

import java.io.IOException;
import java.util.List;

import com.yunmel.util.scan.PackUtils;
import com.yunmel.web.annotation.Controller;

public class PackageScanTest {

	static String packName = "com.yummel.app";

	public static void main(String[] args) throws IOException {
		List<Class<?>> classSaveContrllerPaths = PackUtils.getClassListByAnnotation(packName, Controller.class);
		System.out.println(classSaveContrllerPaths.size());
	}
}
