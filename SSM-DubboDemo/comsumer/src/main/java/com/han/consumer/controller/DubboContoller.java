package com.han.consumer.controller;

import com.han.consumer.service.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/dubbo")
public class DubboContoller {
    private static final Logger LOGGER = LoggerFactory.getLogger(DubboContoller.class);

    @Autowired
    DubboService dubboService;

    @ResponseBody
    @RequestMapping("/test")
    public String testDubbo() {
        dubboService.testDubbo();
        LOGGER.info("Dubbo rpc result : {}");
        return "Dubbo rpc doing normally";
    }
}
