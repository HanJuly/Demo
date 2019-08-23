package com.han.provide.services;

import com.alibaba.dubbo.config.annotation.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public String getName() {
        return "HanJuly-Guo";
    }
}
