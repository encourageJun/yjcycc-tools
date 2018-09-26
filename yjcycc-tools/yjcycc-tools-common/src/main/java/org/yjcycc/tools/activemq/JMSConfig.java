package org.yjcycc.tools.activemq;

import org.apache.activemq.ActiveMQConnection;

/**
 * JMS的配置信息  链接管理信息
 * @author Rosun
 *
 */
public class JMSConfig {

	public static final String USERNAME = ActiveMQConnection.DEFAULT_USER;// 默认连接用户名
	
	public static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;// 默认连接密码
	// 默认连接地址( JMS服务器安装在本机)
	public static final String BROKEURL = "tcp://127.0.0.1:61616?tcpNoDelay=true";

	public static final String DEFAULT_QUEUE_NAME = "DEFAULT_QUEUE_NAME";

	public static final String OPEN_BALL_11X5_QUEUE_NAME = "OPEN_BALL_11X5_QUEUE_NAME";

}
