package org.yjcycc.tools.zk.prop;

import org.yjcycc.tools.common.constant.CommonConsist;
import org.yjcycc.tools.zk.constant.ZkNodeConstant;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 系统配置管理信息； 相关参数
 * @author Rosun
 *
 */
public class SystemProperties {

	private static final SystemProperties instance = new SystemProperties();
 
	private static final String SYS_CONFIG_FILE = "system.properties";
	 
	
	private Properties prop = new Properties();

	/** 初始化 */
	private SystemProperties() {
		InputStream is = null;
		try{
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(SYS_CONFIG_FILE);
			prop.load(is);
		}catch(Exception ex){
			ex.printStackTrace();
			throw new RuntimeException("SystemProperties 配置异常：",ex);
		} finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static final SystemProperties getInstance(){
		return instance;
	}
	
 
	public String getPropWithoutEnv(String key){
		return prop.getProperty(key);
	}
	
	/**
	 * 是否启用日志收集功能？
	 * @return
	 */
	public Boolean isEnabledLogger(){
//		logger.enable=false
		String ena = getPropWithoutEnv("logger.enable");
		if(ena == null){
			return false;
		}
		Boolean b = Boolean.parseBoolean(ena);
		return b;
	} 
	
	/**
	 * 是否启用mongoDB 来记录动作业务数据？
	 * @return
	 */
	public Boolean isEnabledMongoDB(){
//		mongodb.enable=false
		String ena = getPropWithoutEnv("mongodb.enable");
		if(ena == null){
			return false;
		}
		Boolean b = Boolean.parseBoolean(ena);
		return b;
	} 
	
	/**
	 * 获取接收dlq邮件的email地址
	 * @return
	 */
	public static String getDlqEmails() {
		String emails = "";
		SystemProperties m = SystemProperties.getInstance();
		emails = m.getPropWithoutEnv("dlq.mail.emails." + SystemProperties.getEnviroment());
		return emails;
	}
	
	/**
	 * 获取接收dlq短信的手机号码
	 * @return
	 */
	public static String getDlqMobiles() {
		String mobiles = "";
		SystemProperties m = SystemProperties.getInstance();
		mobiles = m.getPropWithoutEnv("dlq.sms.mobiles." + SystemProperties.getEnviroment());
		return mobiles;
	}
	
	/**
	 * 获取dlq邮件开关
	 * @return
	 */
	public static String getDlqOpenMailStatus() {
		String openMailStatus = "";
		SystemProperties m = SystemProperties.getInstance();
		openMailStatus = m.getPropWithoutEnv("dlq.openmail.status." + SystemProperties.getEnviroment());
		return openMailStatus;
	}
	
	/**
	 * 获取dlq短信开关
	 * @return
	 */
	public static String getDlqOpenSmsStatus() {
		String openSmsStatus = "";
		SystemProperties m = SystemProperties.getInstance();
		openSmsStatus = m.getPropWithoutEnv("dlq.opensms.status." + SystemProperties.getEnviroment());
		return openSmsStatus;
	}

	
	/**
	 * 获取环境；
	 * 
	 * @return  返回  prod | devp | test | uat 4者之一
	 */
	public static String getEnviroment(){
		return System.getProperty(ZkNodeConstant.JVM_SYS_PROPERTY_KEY_ENV);
	}

	public static void setEnviroment(String env0){
		System.setProperty(ZkNodeConstant.JVM_SYS_PROPERTY_KEY_ENV, env0);
	}
}
