package org.yjcycc.tools.common.gis;

import com.esri.core.geometry.OperatorContains;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
/**
 * GIS几何元素基础对象，应用于GIS计算
 * @author HeQiang
 *
 */
public abstract class Geometry<T extends com.esri.core.geometry.Geometry> {
	/**
	 * 地球半径
	 */
	public static final double EARTHRADIUS = 6370996.81d;
	/**
	 * 将度转化为弧度
	 * @param degree
	 * @return
	 */
	public static double degreeToRad(double degree) {
		return Math.PI * degree/180; 
	}
	/**
	 * 将弧度转化为度
	 * @param rad
	 * @return
	 */
	public static double radToDegree(double rad) {
		return (180 * rad) / Math.PI;
	}
	
	protected abstract T toEsri();
	
	protected double distance(Point src, Point tag) {
		return new BaiduOperatorDistance().execute(src, tag, null);
	}
	
	protected boolean contains(Polygon polygon, Point point) {
		return OperatorContains.local().execute(polygon, point, null, null);
	}
}
