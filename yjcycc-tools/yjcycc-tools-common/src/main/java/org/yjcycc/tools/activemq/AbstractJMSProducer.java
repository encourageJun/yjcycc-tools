package org.yjcycc.tools.activemq;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;

/**
 * 消息推送生产者；<br>
 * 注意：在session中使用CLIENT_ACKNOWLEDGE签收模式，如果消息不能被正确处理，请调用Session.recover()方法，以便执行重试； <br>
 * 兼容 Queue 和Topic 两种模型；
 * @author Rosun
 *
 */

/**
 * @author Rosun
 *
 */
public abstract class AbstractJMSProducer implements Lifecycle, InitializingBean {
	
	private static Logger logger = LoggerFactory.getLogger(AbstractJMSProducer.class);
	// 连接工厂
	private ConnectionFactory connectionFactory;
	// 连接
	private Connection connection = null;
	// 会话 接受或者发送消息的线程
	protected Session session;
	// 消息的目的地
	private Destination destination;
	// 消息生产者
	protected MessageProducer messageProducer;
	
	// 消息的目的地
	private String destinationName ;

	private transient boolean isRun = false;
	
	
	/**
	 * 声明需要使用的队列（or topic）名称/消息通信的通道
	 * @return
	 */
	public abstract String specifyDestinationName();
	
	
	/**
	 * 声明需要使用队列Queue  还是    话题  Topic
	 * @return
	 */
	public abstract Class<?extends Destination>  specifyDestinationClass();
	
	
	private String brokerUrl = JmsProperties.getInstance().getActivemqJmsBrokerUrl();
	
	
	/**
	 * 指定连接地址URL
	 * @param url
	 */
	public void setBrokerUrl(String url){
		this.brokerUrl = url;
	}

	@Override
	public boolean isRunning() {
		return isRun;
	}

	@Override
	@PostConstruct
	public void start() {
		
		try {
			this.afterPropertiesSet();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		// 实例化连接工厂
		connectionFactory = new ActiveMQConnectionFactory(JMSConfig.USERNAME, JMSConfig.PASSWORD,brokerUrl);
		try {
			// 通过连接工厂获取连接
			connection = connectionFactory.createConnection();
			// 启动连接
			connection.start();
			// 创建session
			session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			// 创建一个名称为HelloWorld的消息队列
			destination =  null;
			
			if(Queue.class.equals(specifyDestinationClass())){
				destination = session.createQueue(destinationName);
			}else{
				destination = session.createTopic(destinationName);
			}
			
			// 创建消息生产者
			messageProducer = session.createProducer(destination);

			isRun = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			isRun = false;
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void stop() {
		if (messageProducer != null) {
			try {
				messageProducer.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		if (session != null) {
			try {
				session.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

		connectionFactory = null;

		this.isRun = false;

	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//获取子类声明的 通道/队列名称
		destinationName = specifyDestinationName();
		
		if(this.destinationName == null){
			this.destinationName = JMSConfig.DEFAULT_QUEUE_NAME;
		}
		logger.info("destinationName="+destinationName);
	}

	/**
	 * 创建map消息
	 * @return
	 * @throws JMSException
	 */
	public MapMessage createMessage() throws JMSException {
		return session.createMapMessage();
	}
}