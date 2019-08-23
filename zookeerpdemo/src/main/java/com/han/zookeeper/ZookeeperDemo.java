package com.han.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

public class ZookeeperDemo {

    @Test
    public void testCreate() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("192.68.72.100:2181,192.168.72.110:2181,192.168.72.120:2181", 100, 100, retryPolicy);
        curatorFramework.start();
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/hello/world");
        curatorFramework.close();
    }

    @Test
    public void testCreateTemp() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("192.68.72.100:2181,192.168.72.110:2181,192.168.72.120:2181", 100, 100, retryPolicy);
        curatorFramework.start();
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/hello2/world");
        curatorFramework.close();
    }

    @Test
    public void testUpate() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("192.68.72.100:2181,192.168.72.110:2181,192.168.72.120:2181", 100, 100, retryPolicy);
        curatorFramework.start();
        curatorFramework.setData().forPath("/hello","123".getBytes());
        curatorFramework.close();
    }

    @Test
    public void testGet() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("192.68.72.100:2181,192.168.72.110:2181,192.168.72.120:2181", 100, 100, retryPolicy);
        curatorFramework.start();
        byte[] s = curatorFramework.getData().forPath("/hello");
        System.out.println(new String(s));
        curatorFramework.close();
    }

    @Test
    public void testWather() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 1);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("192.68.72.100:2181,192.168.72.110:2181,192.168.72.120:2181", 100, 100, retryPolicy);
        curatorFramework.start();
        TreeCache cache = new TreeCache(curatorFramework,"/hello");
        cache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                ChildData s = treeCacheEvent.getData();
                switch (treeCacheEvent.getType()){
                    case NODE_ADDED:
                        System.out.println("NODE_ADDED===="+new String(s.getData()));
                        break;
                    case NODE_REMOVED:
                        System.out.println("NODE_REMOVED===="+new String(s.getData()));
                        break;
                    case NODE_UPDATED:
                        System.out.println("NODE_UPDATED===="+new String(s.getData()));
                        break;
                    default:
                        System.out.println("No event find..........");
                }
            }
        });
        cache.start();
        Thread.sleep(60000);
        curatorFramework.close();
    }
}
