package org.yjcycc.tools.common.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class CombineUtil {

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
	
}
