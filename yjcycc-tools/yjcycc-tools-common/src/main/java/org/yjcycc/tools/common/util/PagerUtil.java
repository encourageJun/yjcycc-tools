package org.yjcycc.tools.common.util;

import org.yjcycc.tools.common.Pager;

import com.github.pagehelper.PageInfo;

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
