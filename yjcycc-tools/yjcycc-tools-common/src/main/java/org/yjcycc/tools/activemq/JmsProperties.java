package org.yjcycc.tools.activemq;

import org.apache.log4j.Logger;
import org.yjcycc.tools.zk.prop.SystemProperties;

import java.util.Properties;

public class JmsProperties {

	private static Logger logger = Logger.getLogger(JmsProperties.class);

	private static final JmsProperties instance = new JmsProperties();

	private static final String RMI_CONFIG_FILE = "jms.properties";

	/**
	 * activemq 链接url
	 */
	private String activemqJmsBrokerUrl;
	
	/** 初始化 */
	private JmsProperties() {
		try {
			Properties prop = new Properties();
			prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(RMI_CONFIG_FILE));
			String dotenv = "." + SystemProperties.getEnviroment();

			activemqJmsBrokerUrl = (prop.getProperty("activemq.jms.broker.url" + dotenv));

			logger.info(">>>>>>>>>>>>>>>>> jmsBrokerUrl:" + activemqJmsBrokerUrl);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("RemoteConfig 配置异常：", e);
		}
	}
	
	public static final JmsProperties getInstance() {
		return instance;
	}

	public String getActivemqJmsBrokerUrl() {
		return activemqJmsBrokerUrl;
	}

	public void setActivemqJmsBrokerUrl(String activemqJmsBrokerUrl) {
		this.activemqJmsBrokerUrl = activemqJmsBrokerUrl;
	}

}
