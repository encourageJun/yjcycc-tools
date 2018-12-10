package org.yjcycc.tools.redis;

import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * redis 连接池实现<br/>
 * <per> 注意：
 * 	Jedis操作步骤如下：
 * 	1->获取Jedis实例需要从JedisPool中获取；
 * 	2->用完Jedis实例需要返还给JedisPool；
 * 	3->如果Jedis在使用过程中出错，则也需要还给JedisPool；
 * 	</per> 
 * @author luoshan
 */
@SuppressWarnings("deprecation")
public class CachePool {

//	JedisSentinelPool pool;
	JedisPool pool;
	private static final CachePool cachePool = new CachePool();

	/** 单例模式 */
	public static CachePool getInstance() {
		return cachePool;
	}
	
	public static final String REDIS_CONFIG_FILE = "redis.properties";
	

	/** 初始化 */
	private CachePool() {
		try{
			JedisPoolConfig config = new JedisPoolConfig(); 
			Properties prop = new Properties();
			String dotenv = "." +  "devp";
			prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(REDIS_CONFIG_FILE));
			//System.out.println(prop);
	        //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；  
	        //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。  
	        config.setMaxTotal(Integer.parseInt(prop.getProperty("maxTotal" + dotenv)));
	        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。  
	        config.setMaxIdle(Integer.parseInt(prop.getProperty("maxIdle" + dotenv)));  
	        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；  
	        config.setMaxWaitMillis(Integer.parseInt(prop.getProperty("maxWaitMillis" + dotenv)));
	        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；  
	        config.setTestOnBorrow(Boolean.parseBoolean(prop.getProperty("testOnBorrow" + dotenv)));  
	        pool = new JedisPool(config, prop.getProperty("poolIP" + dotenv), Integer.parseInt(prop.getProperty("poolPort" + dotenv))); 
		}catch(Exception ex){
			ex.printStackTrace();
			throw new IllegalStateException("Redis 服务初始化异常：",ex);
		}
	}

	public static void main(String[] args) {
		CachePool.getInstance().getJedis();
	}
	
	public Jedis getJedis() {
		Jedis jedis = null;
		boolean borrowOrOprSuccess = true;
		try {
			jedis = pool.getResource();
		} catch (JedisConnectionException e) {
			borrowOrOprSuccess = false;
			//异常时也要释放redis对象
			if (jedis != null){
				pool.returnBrokenResource(jedis);
			}
		} finally {
			//返还到连接池
			if (borrowOrOprSuccess){
				pool.returnResource(jedis);
			}
				
		}
		jedis = pool.getResource();
		return jedis;
	}

//	public JedisSentinelPool getJedisPool() {
//		return this.pool;
//	}
	
	public JedisPool getJedisPool() {
		return this.pool;
	}
}
