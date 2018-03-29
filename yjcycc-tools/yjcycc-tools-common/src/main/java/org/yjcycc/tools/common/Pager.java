package org.yjcycc.tools.common;

import java.io.Serializable;
import java.util.List;

public class Pager<E> implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8358604945757874578L;

	/**
	 * 页码，从1开始
	 */
	private int pageNum;
	/**
     * 页面大小
     */
	private int pageSize;
	/**
     * 总数
     */
	private long totalCount;
	/**
     * 结果集
     */
	private List<E> list;

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}
	
}
