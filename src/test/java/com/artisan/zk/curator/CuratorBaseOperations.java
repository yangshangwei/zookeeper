package com.artisan.zk.curator;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CuratorBaseOperations extends CuratorStandaloneBase {


    /**
     * 递归创建子节点
     */
    @SneakyThrows
    @Test
    public void testCreateNode()  {
        CuratorFramework curatorFramework = getCuratorFramework();

        String nodeName = "/artisan-node";
        String path = curatorFramework.create()
                .creatingParentsIfNeeded()
                .forPath(nodeName,"value".getBytes());
        log.info("curator create node :{}  successfully.", path);
    }

    /**
     * 递归创建子节点
     */
    @SneakyThrows
    @Test
    public void testCreateWithParent()  {
        CuratorFramework curatorFramework = getCuratorFramework();

        String pathWithParent = "/artisan-node/artisan-node-sub1/artisan-node-sub1-1";
        String path = curatorFramework.create().creatingParentsIfNeeded().forPath(pathWithParent);
        log.info("curator create node :{}  successfully.", path);
    }


    /**
     * protection 模式，防止由于异常原因，导致僵尸节点
     * @throws Exception
     */
    @SneakyThrows
    @Test
    public void testCreate()  {

        CuratorFramework curatorFramework = getCuratorFramework();
        String forPath = curatorFramework
                .create()
                .withProtection()  // 防止僵尸节点
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL).
                        forPath("/curator-node", "data".getBytes());
        log.info("curator create node :{}  successfully.", forPath);
    }

    @Test
    public void testGetData() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();

        byte[] bytes = curatorFramework.getData().forPath("/curator-node");
        log.info("get data from  node :{}  successfully.", new String(bytes));
    }



    @Test
    public void testSetData() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();

        curatorFramework.setData().forPath("/curator-node", "changed!".getBytes());
        byte[] bytes = curatorFramework.getData().forPath("/curator-node");
        log.info("get data from  node /curator-node :{}  successfully.", new String(bytes));
    }

    @Test
    public void testDelete() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();

        String pathWithParent = "/node-parent";
        curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath(pathWithParent);
    }

    @Test
    public void testListChildren() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();

        String pathWithParent = "/artisan-node";
        List<String> list = curatorFramework.getChildren().forPath(pathWithParent);
        list.forEach(System.out::println);
    }


    /**
     * 使用默认的 EventThread异步线程处理
     * @throws Exception
     */
    @Test
    public void testThreadPoolByDefaultEventThread() throws Exception {

        CuratorFramework curatorFramework = getCuratorFramework();
        String ZK_NODE="/artisan-node";
        curatorFramework.getData().inBackground((client, event) -> {
            log.info(" background: {}", new String(event.getData()));
        }).forPath(ZK_NODE);;

    }

    /**
     * 使用自定义线程池
     * @throws Exception
     */
    @Test
    public void testThreadPoolByCustomThreadPool() throws Exception {

        CuratorFramework curatorFramework = getCuratorFramework();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        String ZK_NODE="/artisan-node";
        curatorFramework.getData().inBackground((client, event) -> {
            log.info(" background: {}", new String(event.getData()));
        },executorService).forPath(ZK_NODE);

     }



}
