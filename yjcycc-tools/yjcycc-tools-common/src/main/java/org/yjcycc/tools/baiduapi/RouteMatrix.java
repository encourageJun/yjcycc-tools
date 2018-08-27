package org.yjcycc.tools.baiduapi;

import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yjcycc.tools.baiduapi.gis.Point;
import org.yjcycc.tools.baiduapi.gis.dto.RouteMatrixDTO;
import org.yjcycc.tools.baiduapi.properties.BaiduApiProperties;
import org.yjcycc.tools.common.util.HttpClientUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RouteMatrix {
	
	private static final Logger logger = LoggerFactory.getLogger(RouteMatrix.class);

	public final static String BAIDU_ROUTE_MATRIX_WALKING="http://api.map.baidu.com/routematrix/v2/walking?";
	
	public final static String BAIDU_ROUTE_MATRIX_DRIVING="http://api.map.baidu.com/routematrix/v2/driving?";
	
	/**
	 * 获取步行导航距离
	 * @param from
	 * @param to
	 * @return
	 */
	public static RouteMatrixDTO[] ruoteWalking(Point from, Point[] to) {
		if (to == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(BAIDU_ROUTE_MATRIX_WALKING);
		sb.append("output=json");
		sb.append("&origins="+from.getY()+","+from.getX());
		sb.append("&destinations=");		
		for(int i=0; i<to.length; i++) {
			if(i!=0) {
				try {
					sb.append(URLEncoder.encode("|", "UTF-8"));
				} catch (Exception e) {
					logger.error("",e);
				}
			}
			sb.append(to[i].getY()).append(",").append(to[i].getX());
		}
		sb.append("&ak="+BaiduApiProperties.getInstance().getPropWithoutEnv("lbs.ak.prod"));
		
		String responseText=null;
		try {
			responseText = new HttpClientUtil().Get(sb.toString());
		} catch (Exception e) {
			logger.error("百度API计算距离异常", e);
		}
		
		if(StringUtils.isBlank(responseText)) {
			return null;
		}
		
		JSONObject jsonObj = JSONObject.fromObject(responseText);
		
		if("0".equals(jsonObj.getString("status"))) {
			JSONArray arr = jsonObj.getJSONArray("result");
			
			RouteMatrixDTO[] voArr = new RouteMatrixDTO[arr.size()];
			
			for(int i=0,j=voArr.length; i<j; i++) {
				voArr[i] = new RouteMatrixDTO();
				voArr[i].setDistance(arr.getJSONObject(i).getJSONObject("distance").getDouble("value"));
				voArr[i].setTime((int)Math.ceil(arr.getJSONObject(i).getJSONObject("duration").getDouble("value")/60));
			}
			
			return voArr;
		} else {
			return null;
		}
	}
	
	/**
	 * 获取自驾导航距离
	 * @param from
	 * @param to
	 * @return
	 */
	public static RouteMatrixDTO[] ruoteDriving(Point from, Point[] to) {
		if (to == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(BAIDU_ROUTE_MATRIX_DRIVING);
		sb.append("output=json");
		sb.append("&origins="+from.getY()+","+from.getX());
		sb.append("&destinations=");		
		for(int i=0; i<to.length; i++) {
			if(i!=0) {
				try {
					sb.append(URLEncoder.encode("|", "UTF-8"));
				} catch (Exception e) {
					logger.error("", e);
				}
			}
			sb.append(to[i].getY()).append(",").append(to[i].getX());
		}
		sb.append("&ak="+BaiduApiProperties.getInstance().getPropWithoutEnv("lbs.ak.prod"));
		
		String responseText=null;
		try {
			responseText = new HttpClientUtil().Get(sb.toString());
		} catch (Exception e) {
			logger.error("百度API计算距离异常", e);
		}
		
		if(StringUtils.isBlank(responseText)) {
			return null;
		}
		
		JSONObject jsonObj = JSONObject.fromObject(responseText);
		
		if("0".equals(jsonObj.getString("status"))) {
			JSONArray arr = jsonObj.getJSONArray("result");
			
			RouteMatrixDTO[] voArr = new RouteMatrixDTO[arr.size()];
			
			for(int i=0,j=voArr.length; i<j; i++) {
				voArr[i] = new RouteMatrixDTO();
				voArr[i].setDistance(arr.getJSONObject(i).getJSONObject("distance").getDouble("value"));
				voArr[i].setTime((int)Math.ceil(arr.getJSONObject(i).getJSONObject("duration").getDouble("value")/60));
			}
			
			return voArr;
		} else {
			return null;
		}
	}
	
}
