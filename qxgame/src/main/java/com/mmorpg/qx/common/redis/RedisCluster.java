package com.mmorpg.qx.common.redis;

import com.mmorpg.qx.common.identify.manager.ServerConfigValue;
import com.mmorpg.qx.common.logger.SysLoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

//@Component
public class RedisCluster {
	private Logger logger = SysLoggerFactory.getLogger(RedisCluster.class);
	private static RedisCluster instance;
	private JedisCluster cluster;

	@PostConstruct
	public void init() {
		instance = this;
		String url = ServerConfigValue.getInstance().getRedisUrl();
		if (StringUtils.isEmpty(url)) {
			return;
		}
		HashSet<HostAndPort> hostAndPorts = new HashSet<>();
		String[] hostPort = url.split(":");
		HostAndPort hostAndPort = new HostAndPort(hostPort[0], Integer.parseInt(hostPort[1]));
		hostAndPorts.add(hostAndPort);
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(ServerConfigValue.getInstance().getRedisPoolMaxTotal());
		poolConfig.setMinIdle(ServerConfigValue.getInstance().getRedisPoolMinIdle());
		poolConfig.setMaxIdle(ServerConfigValue.getInstance().getRedisPoolMaxIdle());
		this.cluster = new JedisCluster(hostAndPorts, 2000, poolConfig);
		//设置Protobuf缓存
		//ProtobufProxy.enableCache(true);
	}

	public void destory() {
		cluster.close();
	}

	private TreeSet<String> keys(String pattern){
		TreeSet<String> keys = new TreeSet<>();
		//获取所有的节点
		Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
		//遍历节点 获取所有符合条件的KEY
		for (String k : clusterNodes.keySet()) {
			JedisPool jp = clusterNodes.get(k);
			Jedis connection = jp.getResource();
			try {
				keys.addAll(connection.keys(pattern));
			} catch(Exception e) {
			} finally{
				connection.close();//用完一定要close这个链接！！！
			}
		}
		return keys;
	}

	public void clearAllData() {
		TreeSet<String> keys=keys("*");
		//遍历key  进行删除  可以用多线程
		for(String key:keys){
			cluster.del(key);
		}
	}

	public Double zscore(String key, String member) {
		try {
			return cluster.zscore(key, member);
		} catch (JedisException e) {
			logger.error("", e);
			throw new JedisException(e);
		}
	}

	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		try {
			return cluster.zrangeWithScores(key, start, end);
		} catch (JedisException e) {
			logger.error("", e);
			throw new JedisException(e);
		}
	}

	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		try {
			return cluster.zrevrangeWithScores(key, start, end);
		} catch (JedisException e) {
			logger.error("", e);
			return new HashSet<>(0);
		}
	}

	public Double zincrby(String key, double score, String member) {
		try {
			return cluster.zincrby(key, score, member);
		} catch (JedisException e) {
			logger.error("", e);
			return null;
		}
	}

	public Long zrank(String key, String member) {
		try {
			return cluster.zrank(key, member);
		} catch (JedisException e) {
			logger.error("", e);
			return -1L;
		}
	}

	public long hset(String key, String field, String value) {
		try {
			return cluster.hset(key, field, value);
		} catch (JedisException e) {
			logger.error("", e);
		}
		return -1L;
	}

	public String hget(String key, String field) {
		try {
			return cluster.hget(key, field);
		} catch (JedisException e) {
			logger.error("", e);
			return null;
		}
	}

}
