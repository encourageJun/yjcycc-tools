package org.yjcycc.tools.activemq;

public class ConsumException extends Exception {
	
	
	public ConsumException(){}

	public ConsumException(Exception e) {
		super(e);
	}
	
	
	public ConsumException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1412302585859059911L;

}
