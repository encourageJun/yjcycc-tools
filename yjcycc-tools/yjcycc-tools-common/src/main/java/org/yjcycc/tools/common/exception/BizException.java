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
 * 内部业务异常
 * @author luoshan
 */
public class BizException extends RuntimeException{
 
	private static final long serialVersionUID = 1L;

	public BizException(){
		super();
	}
	
	public BizException(String msg){
		super(msg);
	}
	
	public BizException(String msg,Throwable cause){
		super(msg,cause);
	}
	
	public BizException( Throwable cause){
		super("内部业务逻辑错误",cause);
	}
}
