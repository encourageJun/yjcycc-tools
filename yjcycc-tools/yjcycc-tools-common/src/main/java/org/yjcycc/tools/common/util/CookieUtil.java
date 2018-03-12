package org.yjcycc.tools.common.util;

import javax.servlet.http.Cookie;

import org.yjcycc.tools.common.constant.CommonConsist;

public class CookieUtil {

	public static String getProperty(Cookie[] cookies, String name){  
        if(cookies == null) return null;  
        for (Cookie cookie : cookies) {  
            if(cookie.getName().equals(name)){  
                return cookie.getValue();  
            }  
        }  
        return null;  
    }  
      
    public static String getAppToken(Cookie[] cookies){
	    return getProperty(cookies, CommonConsist.TOKEN_COOKIE);
    }  
      
    public static boolean containsPropery(Cookie[] cookies, String name){  
        if(cookies == null) return false;  
        for (Cookie cookie : cookies) {  
            if(cookie.getName().equals(name)){  
                return true;  
            }  
        }  
        return false;  
    }  
      
    public static boolean containsAppToken(Cookie[] cookies){  
        return containsPropery(cookies, CommonConsist.TOKEN_COOKIE);  
    }
	
}
