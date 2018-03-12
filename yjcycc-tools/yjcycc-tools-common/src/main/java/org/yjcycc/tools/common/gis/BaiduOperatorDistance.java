package org.yjcycc.tools.common.gis;

import java.math.BigDecimal;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.OperatorDistance;
import com.esri.core.geometry.ProgressTracker;
import com.esri.core.geometry.Point;

public class BaiduOperatorDistance extends OperatorDistance {

	@Override
	public double execute(Geometry geom1, Geometry geom2, ProgressTracker progressTracker) {
		Point p1 = (Point)geom1;
		Point p2 = (Point)geom2;
		
		double lon1 = toRad(p1.getX());
		double lat1 = toRad(p1.getY());
		double lon2 = toRad(p2.getX());
		double lat2 = toRad(p2.getY());
		
		double distance = org.yjcycc.tools.common.gis.Geometry.EARTHRADIUS *
				Math.acos((Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)));
		
		
		BigDecimal bd = new BigDecimal(distance);
		bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	
	private double toRad(double degree) {
		return org.yjcycc.tools.common.gis.Geometry .degreeToRad(degree);
	}
}
