package org.yjcycc.tools.activemq;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;

/**
 * JMS消费者；<br>
 * 注意：在session中使用CLIENT_ACKNOWLEDGE签收模式，如果消息不能被正确处理，请调用Session.recover()方法，以便执行重试；<br>
 * 兼容 Queue 和Topic 两种模型；
 * @author Rosun
 *
 */
public abstract class AbstractJMSConsumer implements Lifecycle, Runnable, InitializingBean{
	

	protected static Logger logger = LoggerFactory.getLogger(AbstractJMSConsumer.class);
	
	private ActiveMQConnectionFactory connectionFactory;// 连接工厂
	private Connection connection = null;// 连接

	private Session session;// 会话 接受或者发送消息的线程
	private Destination destination;// 消息的目的地

	private MessageConsumer messageConsumer;// 消息的消费者
	
	private String destinationName = null;
	
	
	private transient boolean isRun = false;
	
	
	private Class<?extends Destination> destClass;
	
	@Override
	public boolean isRunning() {
		return isRun;
	}

	@Override
	@PostConstruct
	public void start() {
		isRun = true;
		logger.info("AbstractJMSConsumer start : isRun = " + isRun);
		//初始化 
		destClass = initialize();
		if(destClass == null){
			throw new IllegalStateException("initialize 方法未返回期待的返回值");
		}
		//启动JMS消费者
		new Thread(this).start();
		
		logger.info("JMSConsumer start @" + destClass + ","+ this.destinationName);
	}

	


	/**
	 * 子类初始化 ;<br>
	 * 必须：子类需要在实现中声明通信地址：  specifyDestinationName ，传入通信地址，通常是queue名称或者topic名称；
	 * @return Topic话题 还是 Queue 队列？？ 不能为空
	 */
	public abstract Class<?extends Destination>  initialize() ;
	

	@Override
	@PreDestroy
	public void stop() {
		logger.info("AbstractJMSConsumer stop@" + destClass + ","+ this.destinationName);
		isRun = false;
		
		preDestroy();
		
	}
	
	
	
	public void run() {
		logger.info("AbstractJMSConsumer run >>> @" + destClass + ","+ this.destinationName );
		// 实例化连接工厂
		connectionFactory = new ActiveMQConnectionFactory(JMSConfig.USERNAME, JMSConfig.PASSWORD,
				JmsProperties.getInstance().getActivemqJmsBrokerUrl());
		
		//覆盖默认的初始 的   最大重试次数（默认是6次，这里修改为子类设定）
		RedeliveryPolicy policy = connectionFactory.getRedeliveryPolicy();
		policy.setMaximumRedeliveries(specifyMaximumRedeliveries());
		policy.setMaximumRedeliveryDelay(300);//延迟300毫秒后重试
		//如果还需要覆盖其他策略，在这里补充；
		
		Message message = null;
		try {
			// 通过连接工厂获取连接
			connection = connectionFactory.createConnection();
			
			// 启动连接
			connection.start();
			// 创建session
			session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			// 创建一个连接HelloWorld的消息队列
			destination = null;
			if(destClass.equals(Queue.class)){
				destination =	session.createQueue(destinationName);
			}else{
				destination =	session.createTopic(destinationName);
			}
			// 创建消息消费者
			messageConsumer = session.createConsumer(destination);
			
			
			//实现一个消息的监听器 //实现这个监听器后，以后只要有消息，就会通过这个监听器接收到 
			//consumer.setMessageListener(new MessageListener() { }
			//不采用 被动接受方案 罗珊
			
			//不断循环处理消息
			//不断循环处理消息
			while ( this.isRun ) {
				//当程序有能力处理时接收 主动接收
				message = messageConsumer.receive();
				if(message == null ){
					continue;
				}
				try{
					boolean result = doConsume(message);
					if(result){
						message.acknowledge();
					}else{
						session.recover();
					}
				}catch (ConsumException e) {
					//消息没被消费掉，消费的过程中发生了错误；
					session.recover();
					doHandleException(e,message);
				} 
			}
		}catch(JMSException ex){
			//JMS 链接异常处理 here .............
			doHandleException(ex,message);
		}catch(Exception ex){
			//其他 异常处理 here .............
			doHandleException(ex,message);
		} finally{
			logger.error("finally>>>>  isRun="+isRun);
			cleanJMS();
			
			if(this.isRun){
				new Thread(this).start();
			}else{
				preDestroy();
			}
		}
	}

	
	/**
	 * 声明  失败最多重试的次数
	 * @return
	 */
	public abstract int specifyMaximumRedeliveries() ;
	

	public void doHandleException(Exception ex,Message message) {
		//其他 异常处理 here .............
		ex.printStackTrace();
	}

	public void doHandleException(JMSException ex,Message message) {
		//JMS 链接异常处理 here .............网络类问题
		ex.printStackTrace();
	}
	
	public void doHandleException(ConsumException ex,Message message) {
		//消息没被消费掉，消费的过程中发生了错误；程序内部错误，业务处理类问题
		ex.printStackTrace();
		
		
	}

	/**
	 * 子类释放资源
	 */
	public abstract void preDestroy() ;

	/**
	 * 子类消费消息 ;<br>
	 * 注意：不用处理消息的签收或者回退，只需要返回标识消息是否被成功处理的标识即可；处理过程中如有异常，则包装成ConsumException抛出来；<br>
	 * 框架会根据子类所期望的次数（specifyMaximumRedeliveries） 来重试；
	 * @param message（需要子类强转细化）
	 * @throws ConsumException 当且仅当消息不能被正确处理并标记为已受理时 ，抛出ConsumException
	 * @throws JMSException JMS原生异常不用包装，直接抛出来；
	 */
	public  abstract  boolean doConsume(Message message) throws ConsumException ,JMSException;


	/**
	 * 清理释放JMS资源
	 */
	private void cleanJMS() {
		if (messageConsumer != null ) {
			try {
				messageConsumer.close();
			} catch (JMSException e) {
//				e.printStackTrace();
			}
		}
		if (session != null) {
			try {
				session.close();
			} catch (JMSException e) {
//				e.printStackTrace();
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException e) {
//				e.printStackTrace();
			}
		}

		connectionFactory = null;
	}
 

	/**
	 * 声明目的之地址/消息通道  
	 * @param destinationName 通常是queue名称或者topic名称
	 */
	public void specifyDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		
		if(this.destinationName == null){
			this.destinationName = JMSConfig.DEFAULT_QUEUE_NAME;
		}
		logger.info("afterPropertiesSet >>> queueName="+destinationName);
	}
	 
	 
}
