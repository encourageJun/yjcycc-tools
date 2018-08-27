package org.yjcycc.tools.baiduapi.gis;

import java.io.Serializable;

import com.google.common.base.Strings;

/**
 * 点对象
 * @author HeQiang
 *
 */
public class Point extends Geometry<com.esri.core.geometry.Point> implements Serializable{
	private static final long serialVersionUID = 803049766744431481L;
	
	private double x;
	private double y;
	
	public Point(){}
	
	public Point(double x, double y){
		this.setX(x);
		this.setY(y);
	}
	
	/**
	 * 规范字符串构造
	 * @param pointStr
	 * @author 许俊雄
	 */
	public Point(String pointStr){
		if(!Strings.isNullOrEmpty(pointStr)) {
			try {
				String[] startPoints = pointStr.split(",");
				double fromX = 0d;
				double fromY = 0d;
				if (startPoints.length == 2) {
					fromX = Double.parseDouble(startPoints[0]);
					fromY = Double.parseDouble(startPoints[1]);
				}
				this.setX(fromX);
				this.setY(fromY);
			} catch (Exception e) {
				throw new RuntimeException("format Point exception"+e);
			}
		}
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public double getDistance(Point target) {
		return super.distance(this.toEsri(), target.toEsri());
	}

	@Override
	protected com.esri.core.geometry.Point toEsri() {
		return new com.esri.core.geometry.Point(getX(), getY());
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		if(obj instanceof Point) {
			Point tag = (Point)obj;
			try {
				return this.getX() == tag.getX() && this.getY() == tag.getY();
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return this.getX() + "," + this.getY();
	}
}
