package com.artisan.zk.originalClient;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

@Slf4j
public class BaseOperationStandAloneModeTest extends  StandAloneBaseTest{


    private  static final  String  NODE_NAME = "/artisan-node1";


    @Test
    public void testCreate(){
      try{
          ZooKeeper zooKeeper = getZooKeeper();
          String s = zooKeeper.create(NODE_NAME,"artisan-node-value".getBytes(),
                  ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
          log.info("create persistent node {} , result {}" , NODE_NAME, s );
      }catch (Exception e){
          log.error("create Exception {}", e.getMessage());
      }

    }

}
    