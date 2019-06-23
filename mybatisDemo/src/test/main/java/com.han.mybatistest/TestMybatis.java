package com.han.mybatistest;

import com.han.mybatisdemo.Main;
import com.han.mybatisdemo.mapper.UserMapper;
import com.han.mybatisdemo.pojo.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TestMybatis {
    private final static Logger LOGGER = Logger.getLogger(Main.class);
    private SqlSessionFactory sessionFactory;
    private SqlSession sqlSession;
    private InputStream inputStream;

    @Before
    public void before() {
        inputStream = Main.class.getResourceAsStream("/mybatis-config.xml");
        sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sessionFactory.openSession();


    }

    @After
    public void after() {
        try {
            sqlSession.commit();
            sqlSession.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQuery() {
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.getUser(123);
        LOGGER.info("===================================\n\n\n");
        LOGGER.info(user.toString());
        LOGGER.info("===================================\n\n\n");
    }

    @Test
    public void testQueryLike() {
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> users = userMapper.getUserlike("jack");

        LOGGER.info("===================================\n");
        for (User user : users){
            LOGGER.info(user.toString());
        }
        LOGGER.info("\n");
        LOGGER.info("===================================");
    }

    @Test
    public void testInsert() {
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = new User("HanJoy",78997979);
        int  i= userMapper.saveUser(user);

        LOGGER.info("===================================\n");
        LOGGER.info("保存结果为："+i);
        LOGGER.info("\n===================================");
    }

    @Test
    public void testInsertReturnId() {
        //需要自增主键
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = new User("HanJoy",555);
        int  i= userMapper.saveUserReturnId(user);

        LOGGER.info("===================================\n");
        LOGGER.info("保存结果为："+i);
        LOGGER.info("\n===================================");
    }

    @Test
    public void testUpate() {
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = new User("HanJoy_edit",78997979);
        int  i= userMapper.updateUser(user);
        LOGGER.info("===================================\n");
        LOGGER.info("结果为："+i);
        LOGGER.info("\n===================================");
    }

    @Test
    public void testDelte() {
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        int  i= userMapper.deleteUser(78997979);
        LOGGER.info("===================================\n");
        LOGGER.info("结果为："+i);
        LOGGER.info("\n===================================");
    }
}
