package com.han.mybatisdemo.mapper;

import com.han.mybatisdemo.pojo.User;

import java.util.List;


public interface UserMapper {
    public User getUser(int id);
    public List<User> getUserlike(String name);
    public int saveUser(User user);
    public int saveUserReturnId(User user);
    public int updateUser(User user);
    public int deleteUser(int id);
}
