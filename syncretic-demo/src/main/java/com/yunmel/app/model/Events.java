package com.yunmel.app.model;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class Events {
	private int id;
	private String title;
	@JSONField(format="yyyy-MM-dd HH:mm")  
	private Date start;
	@JSONField(format="yyyy-MM-dd HH:mm")  
	private Date end;
	private Boolean allDay;
	private String user;
	private String color;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Boolean getAllDay() {
		return allDay;
	}

	public void setAllDay(Boolean allDay) {
		this.allDay = allDay;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	
}
