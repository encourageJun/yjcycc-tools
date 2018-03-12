package org.yjcycc.tools.redis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

/**
 * @description Redis缓存工具类
 * @author Rosun
 */
public class RedisUtils {

	private static Logger logger = Logger.getLogger(RedisUtils.class);

	/** 默认缓存时间 */
	private static final int DEFAULT_CACHE_SECONDS = 60 * 60 * 24 * 5;// 单位秒 设置成5天
	 

	/**
	 * 释放redis资源
	 * 
	 * @param jedis
	 */
	@SuppressWarnings("deprecation")
	private static void releaseResource(Jedis jedis) {
		if (jedis != null) {
			CachePool.getInstance().getJedisPool().returnResource(jedis);
		}
	}

	/**
	 * 删除Redis中的所有key
	 * 
	 * @param jedis
	 * @throws Exception
	 */
	public static void flushAll() {
		Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			jedis.flushAll();
		} catch (Exception e) {
			logger.error("Cache清空失败：" + e);
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 保存一个对象到Redis中(缓存过期时间:使用此工具类中的默认时间) . <br/>
	 * 
	 * @param key
	 *            键 . <br/>
	 * @param object
	 *            缓存对象 . <br/>
	 * @return true or false . <br/>
	 * @throws Exception
	 */
	public static Boolean put(String key, Object object) {
		return put(key, object, DEFAULT_CACHE_SECONDS);
	}

	/**
	 * 保存一个对象到redis中并指定过期时间
	 * 
	 * @param key
	 *            键 . <br/>
	 * @param object
	 *            缓存对象 . <br/>
	 * @param seconds
	 *            过期时间（单位为秒）.<br/>
	 * @return true or false .
	 */
	public static Boolean put(String key, Object object, int seconds) {
		Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			byte[] bytekey = RedisSerializeUtils.serialize(key);
			jedis.set(bytekey, RedisSerializeUtils.serialize(object));
			jedis.expire(bytekey, seconds);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Cache保存失败：" + e);
			return false;
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 根据缓存键获取Redis缓存中的值.<br/>
	 * 
	 * @param key
	 *            键.<br/>
	 * @return Object .<br/>
	 * @throws Exception
	 */
	public static Object get(String key) {
		Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			byte[] obj = jedis.get(RedisSerializeUtils.serialize(key));
			return obj == null ? null : RedisSerializeUtils.unSerialize(obj);
		} catch (Exception e) {
			logger.error("Cache获取失败：" + e);
			return null;
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 根据缓存键清除Redis缓存中的值.<br/>
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static Boolean del(String key) {
		Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			jedis.del(RedisSerializeUtils.serialize(key));
			if(key instanceof String){
				jedis.del((String)key);
			}
			return true;
		} catch (Exception e) {
			logger.error("Cache删除失败：" + e);
			return false;
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 根据缓存键清除Redis缓存中的值.<br/>
	 * 
	 * @param keys
	 * @return
	 * @throws Exception
	 */
	public static Boolean del(String... keys) {
		Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			for(String xx : keys){
				jedis.del(RedisSerializeUtils.serialize(xx));
			}
			return true;
		} catch (Exception e) {
			logger.error("Cache删除失败：" + e);
			return false;
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 设置超时时间
	 * @param key
	 * @param seconds
	 *            超时时间（单位为秒）
	 * @return
	 */
	public static Boolean expire(String key, int seconds) {

		Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			jedis.expire(RedisSerializeUtils.serialize(key), seconds);
			return true;
		} catch (Exception e) {
			logger.error("Cache设置超时时间失败：" + e);
			return false;
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 添加一个内容到指定key的hash中
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static Boolean addHash(String key, Object field, Object value) {
		return addHash(key, field, value, DEFAULT_CACHE_SECONDS);
	}
	
	/**
	 * 添加一个内容到指定key的hash中
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static Boolean addHash(String key, Object field, Object value,int expireSeconds) {
		Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			final  byte[] bytekey = RedisSerializeUtils.serialize(key);
			jedis.hset(bytekey, RedisSerializeUtils.serialize(field), RedisSerializeUtils.serialize(value));
			jedis.expire(bytekey, expireSeconds);
			return true;
		} catch (Exception e) {
			logger.error("Cache保存失败：" + e);
			return false;
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 从指定hash中拿一个对象
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public static Object getHash(String key, Object field) {
		Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			byte[] obj = jedis.hget(RedisSerializeUtils.serialize(key), RedisSerializeUtils.serialize(field));
			return RedisSerializeUtils.unSerialize(obj);
		} catch (Exception e) {
			logger.error("Cache读取失败：" + e);
			return null;
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 从hash中删除指定filed的值
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public static Boolean delHash(String key, Object field) {
		Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			long result = jedis.hdel(RedisSerializeUtils.serialize(key), RedisSerializeUtils.serialize(field));
			return result == 1 ? true : false;
		} catch (Exception e) {
			logger.error("Cache删除失败：" + e);
			return null;
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 拿到缓存中所有符合pattern的key
	 * 
	 * @param pattern
	 * @return
	 */
	public static Set<byte[]> keys(String pattern) {
		Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			Set<byte[]> allKey = jedis.keys(RedisSerializeUtils.serialize("*" + pattern + "*"));
			//jedis.keys(("*" + pattern + "*").getBytes());
			return allKey;
		} catch (Exception e) {
			logger.error("Cache获取失败：" + e);
			return new HashSet<byte[]>();
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 获得hash中的所有key value
	 * 
	 * @param key
	 * @return
	 */
	public static Map<byte[], byte[]> getAllHash(String key) {
		Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			Map<byte[], byte[]> map = jedis.hgetAll(RedisSerializeUtils.serialize(key));
			return map;
		} catch (Exception e) {
			logger.error("Cache获取失败：" + e);
			return null;
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 判断一个key是否存在
	 * 
	 * @param key
	 * @return
	 */
	public static Boolean exists(String key) {
		Jedis jedis = null;
		Boolean result = false;
		try {
			jedis = CachePool.getInstance().getJedis();
			result = jedis.exists(RedisSerializeUtils.serialize(key));
			return result;
		} catch (Exception e) {
			logger.error("Cache获取失败：" + e);
			return false;
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * @description 获取key存活时间，单位为秒
	 * @param key
	 * @return
	 */
	public static Long ttl(String key) {
		Jedis jedis = null;
		Long ttl = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			ttl = jedis.ttl(RedisSerializeUtils.serialize(key));
			return ttl;
		} catch (Exception e) {
			logger.error("Cache获取失败：" + e);
			return null;
		} finally {
			releaseResource(jedis);
		}
	}

	/** 
     * 向Set<内装String>集合添加一个或多个成员，返回添加成功的数量 
     * @param key 
     * @param members 
     * @return Long 
     */  
    public static Long addSet(String key, String... members){   
        Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			Long value = jedis.sadd(key, members);  
			jedis.expire(key, DEFAULT_CACHE_SECONDS);
			return value ;
		} catch (Exception e) {
			logger.error("操作失败：" + e);
			return 0l;
		} finally {
			releaseResource(jedis);
		}
    } 
    
    /** 
     * 向Set<内装String>集合添加一个或多个成员，返回添加成功的数量 
     * @param key 
     * @param members 
     * @return Long 
     */  
    public static Long addSet(String key,int seconds, String... members){   
        Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			Long value = jedis.sadd(key, members);
			jedis.expire(key, seconds);
			return value ;
		} catch (Exception e) {
			logger.error("操作失败：" + e);
			return 0l;
		} finally {
			releaseResource(jedis);
		}
    } 
      
    /** 
     * 向Set<内装String>集合添加一个或多个成员，返回添加成功的数量 
     * @param key 
     * @param members 
     * @return Long 
     */  
    public static Long sadd(String key, String... members){   
        Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			Long value = jedis.sadd(key, members);
//			jedis.expire(key, seconds);
			return value ;
		} catch (Exception e) {
			logger.error("操作失败：" + e);
			return 0l;
		} finally {
			releaseResource(jedis);
		}
    } 
      
    /** 
     * 返回集合中的所有成员 Set<内装String> 
     * @param key 
     * @return Set<内装String> 
     */  
    public static Set<String> getSetMembers(String key){  
    	 Jedis jedis = null;
 		try {
 			jedis = CachePool.getInstance().getJedis();
 			Set<String> values = jedis.smembers(key);  
 			return values ;
 		} catch (Exception e) {
 			logger.error("操作失败：" + e);
 			return null;
 		} finally {
 			releaseResource(jedis);
 		}
    }  
      
    /** 
     * 判断 member 元素是否是集合Set<内装String>  key 的成员，在集合中返回True 
     * @param key 
     * @param member 
     * @return Boolean 
     */  
    public static Boolean isSetMember(String key, String member){  
    	Jedis jedis = null;
 		try {
 			jedis = CachePool.getInstance().getJedis();
 			Boolean value = jedis.sismember(key, member);  
 			return value ;
 		} catch (Exception e) {
 			logger.error("操作失败：" + e);
 			return false;
 		} finally {
 			releaseResource(jedis);
 		}
    }  
    /** 
     * 移除集合Set<内装String>中一个或多个成员 
     * @param key 
     * @param members 
     * @return 
     */  
    public static boolean delSetMembers(String key, String... members){  

        Jedis jedis = null;
 		try {
 			jedis = CachePool.getInstance().getJedis();
 			Long value = jedis.srem(key, members);  
 			 if(value > 0){  
 	            return true;  
 	        }  else{
 	        	return false; 
 	        }
 	        
 		} catch (Exception e) {
 			logger.error("操作失败：" + e);
 			return false;
 		} finally {
 			releaseResource(jedis);
 		}
    }  
    
    /** 
     * 移除集合Set<内装String>中的所有成员 
     * @param key 
     * @param members 
     * @return 
     */  
    public static Long delSet(String key){  
        Jedis jedis = null;
 		try {
 			jedis = CachePool.getInstance().getJedis();
 			Long value = jedis.del(RedisSerializeUtils.serialize(key));  
 			return value;
 		} catch (Exception e) {
 			logger.error("操作失败：" + e);
 			return 0L;
 		} finally {
 			releaseResource(jedis);
 		}
    }  
    
    /** 
     * 向Set<内装Object>集合添加一个或多个成员，返回添加成功的数量 
     * @param key 
     * @param members 
     * @return Long 
     */  
    public static Long addSet(String key, byte[]... members){   
        Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			Long value = jedis.sadd(RedisSerializeUtils.serialize(key),members);  
			jedis.expire(key, DEFAULT_CACHE_SECONDS);
			return value ;
		} catch (Exception e) {
			logger.error("操作失败：" + e);
			return 0l;
		} finally {
			releaseResource(jedis);
		}
    } 
    
    /** 
     * 向Set<内装Object>集合添加一个或多个成员，返回添加成功的数量 
     * @param key 
     * @param members 
     * @return Long 
     */  
    public static Long addSet(String key,int seconds, byte[]... members){   
        Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			Long value = jedis.sadd(RedisSerializeUtils.serialize(key), members);
			jedis.expire(RedisSerializeUtils.serialize(key),seconds);
			return value ;
		} catch (Exception e) {
			logger.error("操作失败：" + e);
			return 0l;
		} finally {
			releaseResource(jedis);
		}
    } 
      
    
      
    /** 
     * 返回集合中的所有成员 Set<内装Object> 
     * @param key 
     * @return Set<内装Object> 
     */  
    public static Set<byte[]> getSetMembersObject(String key){  
    	 Jedis jedis = null;
 		try {
 			jedis = CachePool.getInstance().getJedis();
 			Set<byte[]> values = jedis.smembers(RedisSerializeUtils.serialize(key));
 			return values ;
 		} catch (Exception e) {
 			logger.error("操作失败：" + e);
 			return null;
 		} finally {
 			releaseResource(jedis);
 		}
    }  
      
    /** 
     * 判断 member 元素是否是集合Set<内装Object>   key 的成员，在集合中返回True 
     * @param key 
     * @param member 
     * @return Boolean 
     */  
    public static Boolean isSetMember(String key, byte[] member){  
    	Jedis jedis = null;
 		try {
 			jedis = CachePool.getInstance().getJedis();
 			Boolean value = jedis.sismember(RedisSerializeUtils.serialize(key), member);  
 			return value ;
 		} catch (Exception e) {
 			logger.error("操作失败：" + e);
 			return false;
 		} finally {
 			releaseResource(jedis);
 		}
    }  
    /** 
     * 移除集合Set<内装Object> 中一个或多个成员 
     * @param key 
     * @param members 
     * @return 
     */  
    public static boolean delSetMembers(String key, byte[]... members){  
        Jedis jedis = null;
 		try {
 			jedis = CachePool.getInstance().getJedis();
 			Long value = jedis.srem(RedisSerializeUtils.serialize(key), members);  
 			 if(value > 0){  
 	            return true;  
 	        }  else{
 	        	return false; 
 	        }
 	        
 		} catch (Exception e) {
 			logger.error("操作失败：" + e);
 			return false;
 		} finally {
 			releaseResource(jedis);
 		}
    }  
	
    /** 
     * 获取集合<内装String>的成员数 
     * @param key 
     * @return 
     */  
    public static Long cardSetSize(String key){  
        Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			Long value = jedis.scard(key);  
			return value ;
		} catch (Exception e) {
			logger.error("操作失败：" + e);
			return 0l;
		} finally {
			releaseResource(jedis);
		}
    }  
    
    /** 
     * 获取集合<内装Object>的成员数 
     * @param key 
     * @return 
     */  
    public static Long cardSetSize(byte[] key){  
        Jedis jedis = null;
		try {
			jedis = CachePool.getInstance().getJedis();
			Long value = jedis.scard(key);  
			return value ;
		} catch (Exception e) {
			logger.error("操作失败：" + e);
			return 0l;
		} finally {
			releaseResource(jedis);
		}
    }  
    
    
    
    
	public static void main(String[] args) throws Exception {
		//JedisPool pool = CachePool.getInstance().getJedisPool();

		RedisUtils.put("sex", "man");
		RedisUtils.put("name", "David");
		
		StringBuffer sb1 = new StringBuffer("1111111");
		StringBuffer sb2 = new StringBuffer("2222222");
		
		
		//RedisUtils.put("CONFIRM_LETTER_444", "aaaaaaaaaaaaaa");
		Object sex  = RedisUtils.get("sex");
		Object name = RedisUtils.get("name");
		System.out.println("name="+name + "   ,  " + "sex="+sex);
		System.out.println("end");
		
		final String key = "set";
		RedisUtils.addSet(key, RedisSerializeUtils.serialize(sb1),RedisSerializeUtils.serialize(sb2));
		Long size1 = RedisUtils.cardSetSize(key);
		Long size2 = RedisUtils.cardSetSize(RedisSerializeUtils.serialize(key));
		System.out.println("size1="+size1+",size2="+size2);
		
		Set<byte[]> set = RedisUtils.getSetMembersObject(key);
		for(byte[] bytes : set){
			StringBuffer sb = (StringBuffer) RedisSerializeUtils.unSerialize(bytes);
			System.out.println(sb);
		}
		System.out.println("-------------- after delete:" + key);
		RedisUtils.del(key);
		set = RedisUtils.getSetMembersObject(key);
		for(byte[] bytes : set){
			StringBuffer sb = (StringBuffer) RedisSerializeUtils.unSerialize(bytes);
			System.out.println(sb);
		}
//		Object xx = RedisUtils.get("CONFIRM_LETTER_444");
//		System.out.println(xx);
		
	}
}
