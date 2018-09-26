package org.yjcycc.tools.zk.prop;

import org.apache.log4j.Logger;

import java.util.Properties;

public class ZkProperties {

	private static Logger logger = Logger.getLogger(ZkProperties.class);

	private static final ZkProperties instance = new ZkProperties();

	private static final String RMI_CONFIG_FILE = "zk.properties";

	/**
	 * zookeeper 集群 服务链接
	 */
	private String zookeeperConnUrl;
	
	/** 初始化 */
	private ZkProperties() {
		try {
			Properties prop = new Properties();
			prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(RMI_CONFIG_FILE));
			String dotenv = "." + SystemProperties.getEnviroment();

			zookeeperConnUrl = prop.getProperty("zookeeper.url" + dotenv);
			logger.info(">>>>>>>>>>>>>>>>> zookeeperConnUrl:" + zookeeperConnUrl);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("RemoteConfig 配置异常：", e);
		}
	}
	
	public static final ZkProperties getInstance() {
		return instance;
	}

	public String getZookeeperConnUrl() {
		return zookeeperConnUrl;
	}

	public void setZookeeperConnUrl(String zookeeperConnUrl) {
		this.zookeeperConnUrl = zookeeperConnUrl;
	}

}
