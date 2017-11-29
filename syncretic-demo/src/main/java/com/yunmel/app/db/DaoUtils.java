package com.yunmel.app.db;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.yunmel.app.controller.IndexController;
import com.yunmel.app.model.Events;

public class DaoUtils {
	private static Sql2o sql2o;
	private static Connection con;
	static {
		sql2o = new Sql2o("jdbc:mysql://localhost:33352/schedule?useUnicode=true&characterEncoding=utf8", "root", "root");
		con = sql2o.open();
	}

	public static List<Events> findEvents() {
		List<Events> list = con.createQuery("select * from t_event").executeAndFetch(Events.class);
		return list;
	}

	public static void insertEvent(Events e) {
		try (Connection conn = sql2o.beginTransaction()) {
			String insertSql = "insert into t_event(title,start,end,allDay,user,color) values (:title,:start,:end,:allDay,:user,:color)";
			conn.createQuery(insertSql).bind(e).executeUpdate();
			conn.commit();
		}
	}
	
	
	public static void main(String[] args) {
		String filePath = IndexController.class.getClassLoader().getResource("data.json").getPath();
		File file = new File(filePath);
		try {
			String content = Files.readFirstLine(file, Charsets.UTF_8);
			List<Events> list = JSONArray.parseArray(content, Events.class);
			for (Events e : list) {
				DaoUtils.insertEvent(e);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Events findById(Integer id) {
		Events e = con.createQuery("select * from t_event where id=:id").addParameter("id", id).executeAndFetchFirst(Events.class);
		return e;
	}

	public static void updateEvent(Events e) {
		try (Connection conn = sql2o.beginTransaction()) {
			String updateSql = "update t_event set title=:title,start=:start,end=:end,allDay=:allDay,user=:user,color=:color where id=:id";
			conn.createQuery(updateSql).bind(e).executeUpdate();
			conn.commit();
		}
	}

	public static Integer deleteEvent(Integer id) {
		try (Connection conn = sql2o.beginTransaction()) {
			String deleteSql = "delete from t_event where id=:id";
			conn.createQuery(deleteSql).addParameter("id", id).executeUpdate();
			conn.commit();
		}
		return 1;
	}
}