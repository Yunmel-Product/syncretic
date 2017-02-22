package com.yunmel.web.dispatcher;


import com.yunmel.web.ModelAndView;

public interface HandlerAdapter {
    ModelAndView process(Parameter parameter) throws Exception;
}
