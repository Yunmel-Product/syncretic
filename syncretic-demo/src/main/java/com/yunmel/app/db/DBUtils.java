package com.yunmel.app.db;

import com.yunmel.extreme.db.QuerySession;
import com.yunmel.extreme.db.SessionFactory;

public class DBUtils {
	public static QuerySession getQuerySession() {
		return new SessionFactory().openQuerySession();
	}
}