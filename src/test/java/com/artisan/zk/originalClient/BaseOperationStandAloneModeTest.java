package com.artisan.zk.originalClient;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

@Slf4j
public class BaseOperationStandAloneModeTest extends  StandAloneBaseTest{


    private  static final  String  NODE_NAME = "/artisan-node";


    /**
     * 同步创建节点
     */
    @Test
    public void testCreate(){
      try{
          // 获取zk实例
          ZooKeeper zooKeeper = getZooKeeper();
          // 创建节点
          String s = zooKeeper.create(NODE_NAME,"artisan-node-value".getBytes(),
                  ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
          log.info("create persistent node {} , result {}" , NODE_NAME, s );
      }catch (Exception e){
          log.error("create Exception {}", e.getMessage());
      }
    }

    @SneakyThrows
    @Test
    public void testDelete(){
        // -1 代表匹配所有版本，直接删除
        // 任意大于 -1 的代表可以指定数据版本删除
        // if the given version is -1, it matches any node's versions
        getZooKeeper().delete(NODE_NAME,-1);
    }


    @SneakyThrows
    @Test
    public void testSetData() {
        // 修改前数据
        Stat stat = new Stat();
        byte[] data = getZooKeeper().getData(NODE_NAME, null, stat);
        log.info("data before change: " + new String(data));
        int version = stat.getVersion();
        log.info("data version {} " , version);

        // 修改数据
        Stat newStat = getZooKeeper().setData(NODE_NAME, "Modify-Data".getBytes(), version);
        log.info("new stat version info {} " , newStat.getVersion());
        log.info("data after change: {} " , new String(getZooKeeper().getData(NODE_NAME, null, newStat)));
    }



    @SneakyThrows
    @Test
    public void testGetWithOutWatch(){
        byte[] data = getZooKeeper().getData(NODE_NAME, null, null);
        log.info("data {}" , new String(data));
    }


    @SneakyThrows
    @Test
    public void testGetWithWatch(){

        Watcher  watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 监听NodeDataChanged事件
                if (event.getPath() != null && event.getPath().equals(NODE_NAME)
                        && event.getType() == Watcher.Event.EventType.NodeDataChanged){
                    log.info("path {} changed watched " , NODE_NAME);
                    // 监听一旦触发就会失效，因此需要重新监听
                    try {
                        byte[] data = getZooKeeper().getData(NODE_NAME, this, null);
                        log.info("监听触发后的操作-- data: {}",new String(data));
                    } catch (Exception e) {
                        log.info("getData Error {} " , e.getMessage());
                    }
                }
            }
        };

        // 获取节点数据
        byte[] data = getZooKeeper().getData(NODE_NAME, watcher, null);
        log.info("data {}" , new String(data));
    }





    @SneakyThrows
    @Test
    public void testCreateAsyn(){
        getZooKeeper().create(NODE_NAME, "DATA_VALUE".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                (rc, path, ctx, name) -> {
                    String currentThreadName = Thread.currentThread().getName();
                    log.info("currentThreadName {} , rc {} , path {} , ctx {} , name {} " , currentThreadName, rc , path ,ctx ,name );
                }, "ARTISAN");

        byte[] data = getZooKeeper().getData(NODE_NAME, null, null);
        log.info("data {}" , new String(data));
    }
}
    