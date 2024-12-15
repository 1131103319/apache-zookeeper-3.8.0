package org.apache.zookeeper.my_test;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class TestClientWatch {
    public static void main(String[] args) throws InterruptedException, KeeperException, IOException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String connectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
        ZooKeeper zooKeeper = new ZooKeeper(connectString, 9999, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("会话建立成功");
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        Stat stat = new Stat();
        Stat stat1 = new Stat();
        String path = zooKeeper.create("/node1", "a".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, stat);
        System.out.println("同步阻塞"+path+" "+stat.toString());
        byte[] data = zooKeeper.getData("/node1", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event.toString());
            }
        }, stat1);
        System.out.println("data: " + new String(data));
        System.out.println("stat1 "+stat1.toString());
        Thread.sleep(1000000);

    }
}
