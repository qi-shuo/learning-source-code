package com.qis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * @author qishuo
 * @date 2021/8/16 11:40 下午
 */
public class RedisDemo {

    public static void main(String[] args) {
        HostAndPort hostAndPort1 = new HostAndPort("127.0.0.1", 7001);
        HostAndPort hostAndPort2 = new HostAndPort("127.0.0.1", 7002);
        HostAndPort hostAndPort3 = new HostAndPort("127.0.0.1", 7003);
        HostAndPort hostAndPort4 = new HostAndPort("127.0.0.1", 7004);
        HostAndPort hostAndPort5 = new HostAndPort("127.0.0.1", 7005);
        HostAndPort hostAndPort6 = new HostAndPort("127.0.0.1", 7006);
        Set<HostAndPort> set = new HashSet<>();
        set.add(hostAndPort1);
        set.add(hostAndPort2);
        set.add(hostAndPort3);
        set.add(hostAndPort4);
        set.add(hostAndPort5);
        set.add(hostAndPort6);
        JedisCluster jedisCluster = new JedisCluster(set);
        System.out.println(jedisCluster.get("name1"));

        jedisCluster.set("name3","test");
        System.out.println(jedisCluster.get("name2"));
    }


}
