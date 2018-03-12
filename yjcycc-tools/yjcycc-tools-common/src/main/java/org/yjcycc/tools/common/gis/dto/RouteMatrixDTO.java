package org.yjcycc.tools.common.gis.dto;

import java.io.Serializable;

public class RouteMatrixDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6711296723444665420L;
	
	private int time;
	private Double distance;
	/**
	 * 单位：分钟
	 * @return
	 */
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	/**
	 * 单位：米
	 * @return
	 */
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}

}
