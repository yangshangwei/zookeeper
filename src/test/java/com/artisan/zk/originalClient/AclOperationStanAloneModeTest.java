package com.artisan.zk.originalClient;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author 小工匠
 * @version 1.0
 * @description: TODO
 * @date 2020/12/1 11:19
 * @mark: show me the code , change the world
 */

@Slf4j
public class AclOperationStanAloneModeTest  extends  StandAloneBaseTest{

    private  static final  String  NODE_NAME = "/artisan-acl-node";


    /**
     * 创建world模式的节点
     */
    @SneakyThrows
    @Test
    public void testCreateNodeWithACL(){

        List<ACL>aclList = new ArrayList<>();
        ACL acl = new ACL();

        Id id = new Id();
        id.setId("anyone");
        id.setScheme("world");

        // 权限
        int perms = ZooDefs.Perms.ADMIN | ZooDefs.Perms.READ ;

        // 绑定
        acl.setId(id);
        acl.setPerms(perms);

        aclList.add(acl);

        String s = getZooKeeper().create(NODE_NAME, "artisan".getBytes(), aclList, CreateMode.PERSISTENT);
        log.info("path {}  created " ,s);



    }


    /**
     *
     * 使用授权模式创建节点
     */
    @SneakyThrows
    @Test
    public void createWithAclTest2() {

        String namePWD = "artisan:artisanPWD";
        // 对连接添加授权信息
        getZooKeeper().addAuthInfo("digest",namePWD.getBytes());

        List<ACL> acLList = new ArrayList<ACL>();
        ACL acl = new ACL();

        Id id = new Id();
        id.setId(namePWD);
        id.setScheme("auth");

        int perms = ZooDefs.Perms.ADMIN  |  ZooDefs.Perms.READ |ZooDefs.Perms.WRITE;
        acl.setId(id);
        acl.setPerms(perms);

        acLList.add(acl);

        String s = getZooKeeper().create("/artisanNode2", "artisan".getBytes(), acLList, CreateMode.PERSISTENT);
        log.info("create path: {}",s);
    }


    @Test
    public void getDataWithAcl() throws KeeperException, InterruptedException {

        String namePWD = "artisan:artisanPWD";

        // 对连接添加授权信息
        getZooKeeper().addAuthInfo("digest",namePWD.getBytes());


        byte[] data = getZooKeeper().getData("/artisanNode2", false, null);

        log.info("GET_DATA : {}",new String(data));
    }


    public static void main(String[] args) throws NoSuchAlgorithmException {
        String sId = DigestAuthenticationProvider.generateDigest("artisan:artisanPWD");
        System.out.println(sId);
        //  -Dzookeeper.DigestAuthenticationProvider.superDigest=artisan:d3gLrY2XgzvXZbJObw+wiWIQDko=
    }


}
    