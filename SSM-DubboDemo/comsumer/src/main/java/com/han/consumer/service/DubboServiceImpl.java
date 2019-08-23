package com.han.consumer.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.han.provide.services.UserService;
import org.springframework.stereotype.Service;

@Service
public class DubboServiceImpl implements DubboService {

    @Reference
    UserService userService;

    @Override
    public void testDubbo() {
       String s = userService.getName();
        System.out.println("Test Dubbo............." + s);
    }
}
