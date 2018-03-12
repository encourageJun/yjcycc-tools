package org.yjcycc.tools.common.util;

public class ExceptionUtil {

	/** 
     * <p> 
     * 将异常堆栈信息以字符串的格式返回 
     * </p> 
     *  
     * @param e 异常对象 
     * @return 
     */  
    public static String createStackTrackMessage(Exception e) {  
        StringBuffer messsage = new StringBuffer();  
        if (e != null) {  
            messsage.append(e.getClass()).append(": ").append(e.getMessage()).append("\n");  
            StackTraceElement[] elements = e.getStackTrace();  
            for (StackTraceElement stackTraceElement : elements) {  
                messsage.append("\t").append(stackTraceElement.toString()).append("\n");  
            }  
        }  
        return messsage.toString();  
    }
	
}
