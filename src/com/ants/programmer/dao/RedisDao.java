package com.ants.programmer.dao;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public class RedisDao {
	public static JedisCluster getJedis() {
		JedisPoolConfig config=new JedisPoolConfig();
		config.setMaxTotal(30);
		config.setMaxIdle(10);
	
		
		JedisCluster jedisCluster=null;
		try {
			Set<HostAndPort> jedisClusterNode=new HashSet<HostAndPort>();
			jedisClusterNode.add(new HostAndPort("127.0.0.1", 6379));
			jedisClusterNode.add(new HostAndPort("127.0.0.1", 6380));
			jedisClusterNode.add(new HostAndPort("127.0.0.1", 6381));
			jedisClusterNode.add(new HostAndPort("127.0.0.1", 6382));
			jedisClusterNode.add(new HostAndPort("127.0.0.1", 6383));
			jedisClusterNode.add(new HostAndPort("127.0.0.1", 6384));
			jedisCluster=new JedisCluster(jedisClusterNode, config);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return jedisCluster;
	}
	
	public static void closeRourse(Jedis jedis) {
		try {
			if (jedis != null) {
				jedis.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
