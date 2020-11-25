package com.artisan.zk.originalClient;


import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class StandAloneBaseTest {

    private static final String ZK_ADDRESS = "192.168.126.131:2181";

    private static final int SESSION_TIMEOUT = 30_000;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    private static ZooKeeper zooKeeper ;

    private static Watcher watcher = event -> {
        if (event.getState() == Watcher.Event.KeeperState.SyncConnected && event.getType() == Watcher.Event.EventType.None){
            log.info("ZK Connected");
            countDownLatch.countDown();
        }
    };


    @Before
    public void init() throws IOException, InterruptedException {
        log.info("start to connect zk server: {}" , ZK_ADDRESS);
        zooKeeper = new ZooKeeper(ZK_ADDRESS, SESSION_TIMEOUT, watcher);
        log.info("connecting to....{}", ZK_ADDRESS);
        countDownLatch.await();
    }


    @After
    public void  test(){
        try {
            TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
    