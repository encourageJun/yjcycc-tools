package org.yjcycc.tools.baiduapi;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class ModelGrid {

	/**
	 * 获取九宫格
	 * @param center
	 * @return
	 * @throws Exception
	 */
	public static String getNineGrid(String center) throws Exception {
		if (StringUtils.isBlank(center)) {
			return null;
		}
		
		String prefix = center.substring(0,6);
		int x = Integer.parseInt(center.substring(center.indexOf("x")+1,center.indexOf("y")));
		int y = Integer.parseInt(center.substring(center.indexOf("y")+1,center.length()));
		
		String east = prefix + "x" + (x+1) + "y" + y; // 东格
		String west = prefix + "x" + (x-1) + "y" + y; // 西格
		String north = prefix + "x" + x + "y" + (y+1); // 北格
		String south = prefix + "x" + x + "y" + (y-1); // 南格
		String eastNorth = prefix + "x" + (x+1) + "y" + (y+1); // 东北
		String eastSouth = prefix + "x" + (x+1) + "y" + (y-1); // 东南
		String westNorth = prefix + "x" + (x-1) + "y" + (y+1); // 西北
		String westSouth = prefix + "x" + (x-1) + "y" + (y-1); // 西南
		
		StringBuffer grids = new StringBuffer();
		grids.append("'").append(center).append("',")
			.append("'").append(east).append("',")
			.append("'").append(west).append("',")
			.append("'").append(north).append("',")
			.append("'").append(south).append("',")
			.append("'").append(eastNorth).append("',")
			.append("'").append(eastSouth).append("',")
			.append("'").append(westNorth).append("',")
			.append("'").append(westSouth).append("'")
			;
		
		return grids.toString();
	}
	
	/**
	 * 合并、去重
	 * @param grids
	 * @return
	 * @throws Exception
	 */
	public static String addAll(String[] grids) throws Exception {
		Set<String> gridSet = new HashSet<String>();
		for (String gridsStr : grids) {
			if (StringUtils.isBlank(gridsStr)) {
				continue;
			}
			String[] gridCodes = gridsStr.split(",");
			for (int i = 0; i < gridCodes.length; i++) {
				gridSet.add(gridCodes[i]);
			}
		}
		
		StringBuffer gridsSB = new StringBuffer();
		for (String gridCode : gridSet) {
			gridsSB.append("'").append(gridCode).append("',");
		}
		
		return gridsSB.substring(0, gridsSB.length()-1);
	}
	
	public static void main(String[] args) {
		try {
			String nineGrid = getNineGrid("grid-3x58y8");
			
			System.out.println(nineGrid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
