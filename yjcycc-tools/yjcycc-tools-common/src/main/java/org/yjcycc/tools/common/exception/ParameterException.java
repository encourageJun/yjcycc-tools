/**
 ******************************************************************************
 *
 * COPYRIGHT. IHAVECAR.COM 2014. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of ihavecar.com.
 *
 ******************************************************************************
 */
package org.yjcycc.tools.common.exception;

/**
 * @description 请求参数异常类
 * @author biao
 * @date 2016年6月4日
 */
public class ParameterException extends RuntimeException {
 
	private static final long serialVersionUID = 1L;

	public ParameterException() {
		super();
	}
	
	public ParameterException(String msg) {
		super(msg);
	}
	
	public ParameterException(Throwable cause) {
		super(cause);
	}
	
	public ParameterException(String msg, Throwable cause) {
		super(msg,cause);
	}
	
}
