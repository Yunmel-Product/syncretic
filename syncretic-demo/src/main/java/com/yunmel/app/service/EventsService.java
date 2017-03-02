package com.yunmel.app.service;

import java.util.List;
import java.util.Random;

import com.yunmel.app.db.DaoUtils;
import com.yunmel.app.model.Events;
import com.yunmel.web.annotation.Service;

@Service
public class EventsService {
	public List<Events> getEvents() {
		return DaoUtils.findEvents();
	}

	private String[] colors = { "#360", "#f30", "#06c" };
	static Random random = new Random();

	public Integer addEvents(Events e) {
//		if (null != e) {
//			Events event = new Events();
//			event.setTitle(e.getTitle());
//			String start = e.getStartDate() + " " + e.getsHour() + ":" + e.geteMinute();
//			String end = "";
//			if (e.getAllDay()) {
//				end = e.getStartDate() + " 23:59";
//			} else {
//				end = e.getEndDate() + " " + e.geteHour() + ":" + e.geteMinute();
//			}
//			event.setStart(DateUtils.parse(start, "MM/dd/yyyy HH:mm"));
//			event.setEnd(DateUtils.parse(end, "MM/dd/yyyy HH:mm"));
//			event.setAllDay(e.getAllDay());
//			event.setColor(colors[random.nextInt(2)]);
//			DaoUtils.insertEvent(event);
//		}
		e.setColor(colors[random.nextInt(2)]);
		DaoUtils.insertEvent(e);
		return 1;
	}

	public Events findById(Integer id) {
		return DaoUtils.findById(id);
	}

	public Integer saveEvent(Events e) {
		if(e.getId() != 0 ){
			DaoUtils.updateEvent(e);
		}else {
			DaoUtils.insertEvent(e);
		}
		return 1;
	}

	public Integer deleteEvent(Integer id) {
		return DaoUtils.deleteEvent(id);
	}

}
