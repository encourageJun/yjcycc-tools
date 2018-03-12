package org.yjcycc.tools.common.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaiduApiProperties {
	
	private static final Logger logger = LoggerFactory.getLogger(BaiduApiProperties.class);

	private static final BaiduApiProperties instance = new BaiduApiProperties();
	 
	private static final String BAIDU_API_FILE = "baidu_api.properties";
	
	private Properties props = new Properties();
	
	public BaiduApiProperties () {
		try {
			props.load(BaiduApiProperties.class.getClassLoader().getResourceAsStream(BAIDU_API_FILE));
		} catch (FileNotFoundException e) {
            logger.error("baidu_api.properties文件未找到");
        } catch (IOException e) {
        	logger.error("出现IOException");
		}
        logger.info("加载properties文件内容完成...........");
        logger.info("properties文件内容：" + props);
	}
	
	public static final BaiduApiProperties getInstance(){
		return instance;
	}
	
	public String getPropWithoutEnv(String key) {
		return props.getProperty(key);
	}
	
	public static String getLbsAk() {
		String ak = "";
		BaiduApiProperties m = BaiduApiProperties.getInstance();
		ak = m.getPropWithoutEnv("lbs.ak.devp");
		return ak;
	}
	
}
