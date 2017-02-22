package com.yunmel.web.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yunmel.util.StringUtils;
import com.yunmel.web.annotation.Delete;
import com.yunmel.web.annotation.Get;
import com.yunmel.web.annotation.Post;
import com.yunmel.web.annotation.Put;
import com.yunmel.web.annotation.RequestMapping;
import com.yunmel.web.annotation.RequestMethod;
import com.yunmel.web.meta.MethodObj;

public class MappingFactory {
	private static Logger LOG = LoggerFactory.getLogger(MappingFactory.class);
	private static Map<String, MethodObj> INSTANCE_MAPPING = new HashMap<String, MethodObj>();
	private static Map<String, Set<String>> REQ_METHOD_MAPPING = new HashMap<String, Set<String>>();

	private MappingFactory() {
	}

	public static MethodObj get(String url) {
		return INSTANCE_MAPPING.get(url);
	}
	
	public static boolean allowMethod(String url,String method){
		return REQ_METHOD_MAPPING.get(url).contains(method);
	}

	public static String getUrlPath(String url){
		if(INSTANCE_MAPPING.containsKey(url) && REQ_METHOD_MAPPING.containsKey(url)){
			return url;
		}
		else {
			UrlPath targetPath = new UrlPath(url);
			for(Entry<String, MethodObj> e : INSTANCE_MAPPING.entrySet()){
				String srcUrl = e.getKey();
				if(srcUrl.contains("{") && srcUrl.contains("}")){
					if((new UrlPath(srcUrl)).equals(targetPath)){
						return srcUrl;
					}
				}
			}
		}
		return null;
	}
	
	public static void addMapping(Class<?> classPath, Object controller) {
		// BeanFactory.addBean(classPath.getName(), controller);
		RequestMapping rm = controller.getClass().getAnnotation(RequestMapping.class);
		String[] values = null != rm ? rm.value() : null;
		List<String> paths = new ArrayList<String>();
		if (null == values || values.length < 1) {
			paths.add("/");
		} else {
			for (String pathValue : values) {
				if (!paths.contains(pathValue)) {
					paths.add(pathValue);
				} else {
					LOG.error("controller 映射重复,class path is [{}],path is [{}].", classPath, pathValue);
				}
			}
		}
		for (String pathValue : paths) {
			if (pathValue.charAt(0) != '/') {
				pathValue = "/" + pathValue;
			}
			if (pathValue.charAt(pathValue.length() - 1) == '/') {
				pathValue = pathValue.substring(1);
			}

			for (Method method : controller.getClass().getMethods()) {
				if (method.getAnnotations().length == 0)
					continue;
				rm = method.getAnnotation(RequestMapping.class);
				String[] mValues = null;
				RequestMethod[] reqMethods = null;
				if (null == rm) {
					Get get = method.getAnnotation(Get.class);
					Post post = method.getAnnotation(Post.class);
					Delete delete = method.getAnnotation(Delete.class);
					Put put = method.getAnnotation(Put.class);

					if (null != get) {
						mValues = get.value();
						reqMethods = new RequestMethod[] { RequestMethod.GET };
					} else if (null != post) {
						mValues = post.value();
						reqMethods = new RequestMethod[] { RequestMethod.POST };
					} else if (null != delete) {
						mValues = delete.value();
						reqMethods = new RequestMethod[] { RequestMethod.DELETE };
					} else if (null != put) {
						mValues = put.value();
						reqMethods = new RequestMethod[] { RequestMethod.PUT };
					}

					if (mValues == null) {
						LOG.info("映射地址错误,class path is [{}],method is [{}]", classPath, method.getName());
						continue;
					}
				} else {
					mValues = rm.value();
					reqMethods = rm.method();
				}
				if (mValues.length == 0) {
					addMapping("/", new MethodObj(classPath.getName(), method), reqMethods);
					continue;
				}
				for (String value : mValues) {
					if (value.length() > 0 && value.charAt(0) != '/') {
						value = "/" + value;
					}
					String uri = pathValue.concat(value);
					if (uri.length() > 1 && uri.charAt(uri.length() - 1) == '/') {
						uri = uri.substring(0, uri.length() - 1);
					}
					addMapping(uri, new MethodObj(classPath.getName(), method), reqMethods);
				}
			}
		}

	}

	private static void addMapping(String uri, MethodObj mObj, RequestMethod[] methods) {
		INSTANCE_MAPPING.put(uri, mObj);
		Set<String> mSet = new HashSet<String>();
		if(null == methods || methods.length == 0){
			mSet.add("GET");
		}else{
			for (RequestMethod method : methods) {
				if (RequestMethod.GET == method) {
					mSet.add("GET");
					LOG.debug("scanned uri [{}] ,method [ GET ].", uri);
				} else if (RequestMethod.POST == method) {
					mSet.add("POST");
					LOG.debug("scanned uri [{}] ,method [ POST ].", uri);
				} else if (RequestMethod.PUT == method) {
					mSet.add("PUT");
					LOG.debug("scanned uri [{}] ,method [ PUT ].", uri);
				} else if (RequestMethod.DELETE == method) {
					mSet.add("DELETE");
					LOG.debug("scanned uri [{}] ,method [ DELETE ].", uri);
				}
			}
		}
		REQ_METHOD_MAPPING.put(uri, mSet);
		LOG.info("request mapping method path : {} - {} - {}", mObj.getClassName(), mObj.getMethod().getName(), uri);
	}
}
class UrlPath {
    static final String DYNA_PART = "";
    private List<String> parts = new ArrayList<String>();
    UrlPath(CharSequence path) {
        String s = path.toString();
        String[] sa = s.split("/");
        for (String item: sa) {
            if (StringUtils.isNotBlank(item)) {
                if (item.startsWith("{") || item.contains(":")) {
                    item = DYNA_PART;
                }
                parts.add(item);
            }
        }
    }

    boolean matches(CharSequence path) {
        return equals(new UrlPath(path));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof UrlPath) {
            UrlPath that = (UrlPath) obj;
            if (parts.size() != that.parts.size()) {
                return false;
            }
            for (int i = parts.size() - 1; i >= 0; --i) {
                if (!matches(parts.get(i), that.parts.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean matches(CharSequence cs1, CharSequence cs2) {
        if (DYNA_PART.equals(cs1)) {
            return true;
        }
        int len = cs1.length();
        if (len != cs2.length()) {
            return false;
        }
        for (int i = len - 1; i >= 0; --i) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}

