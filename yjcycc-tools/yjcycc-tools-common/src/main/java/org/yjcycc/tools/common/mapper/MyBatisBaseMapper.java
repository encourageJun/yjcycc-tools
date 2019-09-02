package org.yjcycc.tools.common.mapper;

import java.util.List;
import java.util.Map;


/**
 * 基本的数据库CURD支持
 * @author 许俊雄
 * @since 2017-04-13
 */
public interface MyBatisBaseMapper<T> {

	T getById(Long id);

	T get(T entity);
	
	List<T> findPager(T entity);
	
	List<T> findPagerByMap(Map<String,Object> map);

	List<T> findList(T entity);

	List<T> findListByMap(Map<String,Object> map);
	
	int insert(T entity);
	
	int update(T entity);

	int deleteById(Long id);
	
	int delete(T entity);
	
	int batchDelete(String ids);
	
}
