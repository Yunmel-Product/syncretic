package com.yunmel.app.controller;

import java.io.IOException;
import java.util.List;

import com.yunmel.app.model.Events;
import com.yunmel.app.service.EventsService;
import com.yunmel.web.annotation.Autowired;
import com.yunmel.web.annotation.Controller;
import com.yunmel.web.annotation.Get;
import com.yunmel.web.annotation.ModelAttribute;
import com.yunmel.web.annotation.Post;
import com.yunmel.web.annotation.RequestMapping;
import com.yunmel.web.annotation.ResponseBody;
import com.yunmel.web.meta.ModelMap;

@Controller
public class IndexController {

	@Autowired
	private EventsService eventsService;
	
	@RequestMapping
	public String index() {
		
		return "index.jsp";
	}

	@Get("/events/add")
	public String add(ModelMap model,String date) {
		System.out.println(date);
		model.addAttribute("selDate", date);
		return "event.jsp";
	}//
	
	@Get("/events/update")
	public String update(ModelMap model,Integer id) {
		System.out.println(id);
		Events e = eventsService.findById(id);
		model.addAttribute("e", e);
		return "event.jsp";
	}//

	@Get("events/list")
	@ResponseBody
	public List<Events> list() throws IOException {
		return eventsService.getEvents();
	}
	
	@Post("events/submit")
	@ResponseBody
	public Integer submit(@ModelAttribute Events events){
		return eventsService.saveEvent(events);
	}
	@Post("events/delete")
	@ResponseBody
	public Integer submit(Integer id){
		return eventsService.deleteEvent(id);
	}
	
}
