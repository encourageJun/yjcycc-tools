package org.yjcycc.tools.redis;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 将用户会话信息保存在Redis中的工具类；<br>
 * 原则上 只通过一个cookie来标识某个用户。一个cookie id即相当于一个session Id
 * @author redis
 * 
 */
public class RedisSessionUtils {

	private static Logger logger = Logger.getLogger(RedisSessionUtils.class);
	/**
	 * 默认过期时间
	 */
	private static final Integer DEFAULT_SESSION_TIMEOUT = 60 * 30;

	/**
	 * 保存session/cookie 对应的中间信息到Context中
	 * @param sessionId
	 * @param sessionObj
	 * @param seconds 超时秒数，如果为null则默认为30分钟
	 * @return
	 */
	public static boolean saveSessionContext(String sessionId, Map<String, Object> sessionObj, Integer seconds) {
		boolean result = false;
		try {
			result = RedisUtils.put(sessionId, sessionObj, seconds);
		} catch (Exception e) {
			logger.error("缓存删除失败：" + e);
		}
		return result;
	}
	
	/**
	 * 保存session/cookie 对应的中间信息到Context中
	 * @param sessionId
	 * @param sessionObj
	 * @return
	 */
	public static boolean saveSessionContext(String sessionId, Map<String, Object> sessionObj) {
		boolean result = false;
		try {
			result = RedisUtils.put(sessionId, sessionObj, DEFAULT_SESSION_TIMEOUT);
		} catch (Exception e) {
			logger.error("缓存删除失败：" + e);
		}
		return result;
	}

	/**
	 * 删除session Context
	 * 
	 * @param sessionId
	 * @return
	 */
	public static boolean deleteSessionContext(String sessionId) {
		boolean result = false;
		try {
			result = RedisUtils.del(sessionId);
		} catch (Exception e) {
			logger.error("缓存删除失败：" + e);
		}
		return result;
	}

	/**
	 * 取session Context
	 * 
	 * @param sessionId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getSessionContext(String sessionId) {
		Map<String, Object> sessionObj = null;
		try {
			sessionObj = (Map<String, Object>) RedisUtils.get(sessionId);
		} catch (Exception e) {
			logger.error("缓存读取失败：" + e);
		}
		return sessionObj;
	}
	
	/**
	 * 更新session Context超时时间
	 * @param sessionId
	 * @return
	 */
	public static boolean updateSessionContextTime(String sessionId) {
		boolean result = false;
		try {
			result = RedisUtils.expire(sessionId, DEFAULT_SESSION_TIMEOUT);
		} catch (Exception e) {
			logger.error("缓存时间更新失败：" + e);
		}
		return result;
	}

	/**
	 * 更新session Context超时时间
	 * @param sessionId
	 * @param seconds
	 * @return
	 */
	public static boolean updateSessionContextTime(String sessionId, Integer seconds) {
		boolean result = false;
		try {
			result = RedisUtils.expire(sessionId, seconds);
		} catch (Exception e) {
			logger.error("缓存时间更新失败：" + e);
		}
		return result;
	}
	
	/**
	 * @description 获取SessionContext存活时间
	 * @param sessionId
	 * @return
	 */
	public static Long getSessionContextTtl(String sessionId) {
		Long ttl = null;
		try {
			ttl = RedisUtils.ttl(sessionId);
		} catch (Exception e) {
			logger.error("获取缓存存活时间失败 ：" + e);
		}
		return ttl;
	}

}
