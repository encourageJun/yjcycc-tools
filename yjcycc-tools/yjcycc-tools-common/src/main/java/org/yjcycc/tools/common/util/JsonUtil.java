package org.yjcycc.tools.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @description JSON实用类
 * @author biao
 * @date 2016年6月4日
 */
public class JsonUtil {
	
	private final static ObjectMapper MAPPER = new ObjectMapper();
	
	static {
		MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		
		//MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true) ;   
		//MAPPER.setSerializationInclusion(Include.NON_NULL); //属性为NULL 不序列化 
		//使用标准  STANDER_DATETIME_TEMPLATE = "yyyy-MM-dd HH:mm:ss" 作为时间对象序列化格式
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MAPPER.setDateFormat(dateFormat);
	}
	
	/**
	 * @description Bean对象转Json字符串
	 * @param bean
	 * @return
	 */
	public static String toJson(Object bean) {
		try {
			//MAPPER.setSerializationInclusion(Include.NON_EMPTY);
			return MAPPER.writeValueAsString(bean);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	/**
	 * @description Json字符串转Bean对象
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T toBean(String json, Class<T> clazz) {
		try {
			return MAPPER.readValue(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	 
	
	/**
	 * @description Json字符串转Bean列表
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> toList(String json, Class<T> clazz) {
		try {
			return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
