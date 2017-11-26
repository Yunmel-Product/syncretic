package com.yunmel.extreme.web.dispatcher;


import com.yunmel.extreme.web.ModelAndView;

public interface HandlerAdapter {
    ModelAndView process(Parameter parameter) throws Exception;
}
