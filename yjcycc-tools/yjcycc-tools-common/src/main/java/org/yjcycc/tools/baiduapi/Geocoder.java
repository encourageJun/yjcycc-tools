package org.yjcycc.tools.baiduapi;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yjcycc.tools.common.gis.dto.BaiduPoiDTO;
import org.yjcycc.tools.common.properties.BaiduApiProperties;
import org.yjcycc.tools.common.util.HttpClientUtil;

import net.sf.json.JSONObject;

public class Geocoder {
	
	private static final Logger logger = LoggerFactory.getLogger(Geocoder.class);
	
	public final static String BAIDU_GEOCODING="http://api.map.baidu.com/geocoder/v2/?";

	/**
	 * 逆地址解析
	 * @param lng 经度
	 * @param lat 纬度
	 * @param coordType 坐标的类型，目前支持的坐标类型包括：bd09ll（百度经纬度坐标）、bd09mc（百度米制坐标）、gcj02ll（国测局经纬度坐标）、wgs84ll（ GPS经纬度）
	 * @return
	 */
	public static BaiduPoiDTO getAddress(double lng, double lat, String coordType) {
		StringBuffer sb = new StringBuffer();
		sb.append(BAIDU_GEOCODING);
		sb.append("output=json");
		sb.append(String.format("&location=%s,%s",lat,lng));
		sb.append("&pois=0");
		
		if(StringUtils.isBlank(coordType)) {
			sb.append("&coordtype="+coordType);
		}
		
		sb.append("&ak="+BaiduApiProperties.getInstance().getPropWithoutEnv("lbs.ak.prod"));
		
		String responseText=null;
		try {
			
			String requestUrl = sb.toString();
			
			logger.info("逆地址请求：" + requestUrl);
			
			responseText = new HttpClientUtil().Get(requestUrl);
		} catch (Exception e) {
			logger.error("", e);
		}
		
		if(StringUtils.isBlank(responseText)) {
			return null;
		}
		
		JSONObject jsonObj = JSONObject.fromObject(responseText);
		
		if(!"0".equals(jsonObj.getString("status"))) {
			return null;	
		} 
		
		BaiduPoiDTO vo = new BaiduPoiDTO();
		
		JSONObject result = jsonObj.getJSONObject("result");	
		vo.setSematicDescription(result.getString("sematic_description"));
		vo.setFormattedAddress(result.getString("formatted_address"));
		vo.setBusiness(result.getString("business"));
		vo.setCityCode(Integer.toString(result.getInt("cityCode")));
		
		JSONObject location = result.getJSONObject("location");
		vo.setLng(location.getDouble("lng"));
		vo.setLat(location.getDouble("lat"));
		
		JSONObject addressComponent = result.getJSONObject("addressComponent");
		vo.setCountry(addressComponent.getString("country"));
		vo.setCountryCode(addressComponent.getString("country_code"));
		vo.setProvince(addressComponent.getString("province"));
		vo.setCity(addressComponent.getString("city"));
		vo.setDistrict(addressComponent.getString("district"));
		vo.setAdcode(addressComponent.getString("adcode"));
		vo.setStreet(addressComponent.getString("street"));
		vo.setStreetNumber(addressComponent.getString("street_number"));
		vo.setDirection(addressComponent.getString("direction"));
		vo.setDistance(addressComponent.getString("distance"));
		
		return vo;
	}
	
}
