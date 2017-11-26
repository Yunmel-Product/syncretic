package com.yunmel.test;

import java.io.IOException;
import java.util.List;

import com.yunmel.extreme.annotation.Controller;
import com.yunmel.extreme.util.PackUtils;

public class PackageScanTest {

	static String packName = "com.yummel.app";

	public static void main(String[] args) throws IOException {
		List<Class<?>> classSaveContrllerPaths = PackUtils.getClassListByAnnotation(packName, Controller.class);
		System.out.println(classSaveContrllerPaths.size());
	}
}
