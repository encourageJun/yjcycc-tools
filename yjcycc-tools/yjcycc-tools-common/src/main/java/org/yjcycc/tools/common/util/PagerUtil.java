package org.yjcycc.tools.common.util;

import com.github.pagehelper.PageInfo;
import org.yjcycc.tools.common.Pager;

public class PagerUtil {

	public static <E> Pager<E> getPager(PageInfo<E> page) {
		Pager<E> pager = new Pager<E>();
		pager.setPageNum(page.getPageNum());
		pager.setPageSize(page.getPageSize());
		pager.setTotalCount(page.getTotal());
		pager.setList(page.getList());
		return pager;
	}
	
}
