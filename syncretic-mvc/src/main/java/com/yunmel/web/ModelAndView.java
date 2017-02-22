package com.yunmel.web;

import com.yunmel.web.meta.ModelMap;
import com.yunmel.web.meta.RespType;

public class ModelAndView {
	private Object view;
	private ModelMap model;
	private RespType rt;

	public ModelAndView() {
		this.model = new ModelMap();
		this.rt = RespType.PAGE;
	}
	
	public ModelAndView(ModelMap model) {
		this.model = model;
		this.rt = RespType.PAGE;
	}
	
	public void add(ModelMap data){
		this.model.addAllAttributes(data);
	}
	
	public ModelMap getModel() {
		return model;
	}

	public RespType getRt() {
		return rt;
	}

	public void setRt(RespType rt) {
		this.rt = rt;
	}

	public Object getView() {
		return view;
	}

	public void setView(Object view) {
		this.view = view;
	}

}
