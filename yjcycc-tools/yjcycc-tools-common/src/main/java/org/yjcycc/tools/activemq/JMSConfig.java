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
	
	public static final String JPUSH_QUEUE_NAME = "jpush";
	
	public static final String RPC_QUEUE_NAME = "rpc";
	
	public static final String TEST_QUEUE_NAME = "test";
	
	
	public static final String DISPATCH_RESULT_TOPIC_NAME = "dspResult";
	
	public static final String DISPATCH_CANCEL_TOPIC_NAME = "dspCancel";
	
	//WEBSOCKET消息中心的队列名称
	public static final String MSGCENTER_WS_QUEUE_NAME= "QUEUE_WEBSOCKET_MSG";	
	
	public static final String AI_MINIBUS_RECOMMEND = "AI_MINIBUS_RECOMMEND";

}
