package com.yunmel.extreme.db;

public class SessionFactory {

	public Session openSession() {
		return Session.getInstanceof();
	}

	public QuerySession openQuerySession() {
		return QuerySession.getInstanceof();
	}
}