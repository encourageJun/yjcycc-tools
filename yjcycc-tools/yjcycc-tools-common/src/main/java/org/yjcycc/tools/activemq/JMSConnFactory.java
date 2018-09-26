package org.yjcycc.tools.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

public class JMSConnFactory {
	private static JMSConnFactory instance;
	private static Connection connection;
	
	private JMSConnFactory() {
	}
	
	public Connection getConnection() {
		if(connection == null) {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					JMSConfig.USERNAME,JMSConfig.PASSWORD, JmsProperties.getInstance().getActivemqJmsBrokerUrl());
			
	        try {
	        	RedeliveryPolicy policy = ((ActiveMQConnectionFactory)connectionFactory).getRedeliveryPolicy();
	    		policy.setMaximumRedeliveries(specifyMaximumRedeliveries());
	    		policy.setMaximumRedeliveryDelay(500);
	    		
				connection = connectionFactory.createConnection();
			} catch (JMSException e) {
				e.printStackTrace();
			}  
		}
        return connection;
	}
	
	/**
	 * 默认连接重试次数（缺省值：6）
	 * @return
	 */
	public int specifyMaximumRedeliveries() {
		return 6;
	}

	public static JMSConnFactory getInstance() {
		if(instance == null) {
			instance = new JMSConnFactory();
		}
		return instance;
	}
}
