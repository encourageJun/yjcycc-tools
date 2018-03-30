package org.yjcycc.tools.common.mapper;

import java.util.List;


/**
 * 基本的数据库CURD支持
 * @author 许俊雄
 * @since 2017-04-13
 */
public interface MyBatisBaseMapper<T> {

	public T get(T entity);
	
	public List<T> findPager(T entity);
	
	public int insert(T entity);
	
	public int update(T entity);
	
	public int delete(T entity);
	
}
