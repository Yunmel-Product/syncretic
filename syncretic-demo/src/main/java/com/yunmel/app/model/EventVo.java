package com.yunmel.app.model;

public class EventVo {
	private int id;
	private String title;
	private String startDate;
	private String endDate;
	private String sHour;
	private String sMinute;
	private String eHour;
	private String eMinute;
	private Boolean allDay;
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
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getsHour() {
		return sHour;
	}
	public void setsHour(String sHour) {
		this.sHour = sHour;
	}
	public String getsMinute() {
		return sMinute;
	}
	public void setsMinute(String sMinute) {
		this.sMinute = sMinute;
	}
	public String geteHour() {
		return eHour;
	}
	public void seteHour(String eHour) {
		this.eHour = eHour;
	}
	public String geteMinute() {
		return eMinute;
	}
	public void seteMinute(String eMinute) {
		this.eMinute = eMinute;
	}
	public Boolean getAllDay() {
		allDay =  null == allDay ? false : allDay;
		return allDay;
	}
	public void setAllDay(Boolean allDay) {
		this.allDay = allDay;
	}
	
}
