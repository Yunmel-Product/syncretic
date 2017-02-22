package com.yunmel.test;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class ScanTest {
	static String packName = "com.yunmel.test";
	public static void main(String[] args) throws IOException {
		 Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packName.replaceAll("\\.", "/"));
		 System.out.println(urls.hasMoreElements());
	}
}
