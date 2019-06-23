package com.han.nettyDemo;

import com.han.nettyDemo.fileserver.HttpFileServerHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class NettyDemoApplicationTests {

	@Test
	public void contextLoads() {
//		HttpFileServerHandler.listFiles(null,new File("D:\\Recursion\\project\\example\\netty_demo\\src\\main\\java\\com\\han\\nettyDemo"));
	    File file = new File(System.getProperty("user.dir")+"\\src\\main\\java\\com\\han\\nettyDemo");
	    System.out.println(file.isDirectory() + "==="+file.getAbsolutePath());
	}

}
