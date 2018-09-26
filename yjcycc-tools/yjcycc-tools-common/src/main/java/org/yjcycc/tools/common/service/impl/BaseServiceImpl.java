package org.yjcycc.tools.common.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.yjcycc.tools.common.Pager;
import org.yjcycc.tools.common.entity.BaseEntity;
import org.yjcycc.tools.common.mapper.MyBatisBaseMapper;
import org.yjcycc.tools.common.service.BaseService;
import org.yjcycc.tools.common.util.PagerUtil;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class BaseServiceImpl<T> implements BaseService<T> {

	protected MyBatisBaseMapper<T> baseMapper;

	public void setBaseMapper(MyBatisBaseMapper<T> baseMapper) {
		this.baseMapper = baseMapper;
	}

//	@Override
	public Pager<T> findPager(Map<String, Object> map, int pageNum, int pageSize) throws RemoteException {
		PageHelper.startPage(pageNum, pageSize);
		List<T> list = baseMapper.findPagerByMap(map);
		
		if (list == null) {
			return null;
		}
		
		return PagerUtil.getPager(new PageInfo<T>(list));
	}

	@Override
	public List<T> findList(T t) throws RemoteException {
		return baseMapper.findList(t);
	}

	@Override
	public List<T> findListByMap(Map<String, Object> map) throws RemoteException {
		return baseMapper.findListByMap(map);
	}

	//	@Override
	public T get(T entity) throws RemoteException {
		return baseMapper.get(entity);
	}

//	@Override
	public void delete(T entity) throws RemoteException {
		baseMapper.delete(entity);
	}
	
	public void saveOrUpdate(T entity) throws RemoteException {
		try {
			Class clazz = clazz = entity.getClass();
//			if (clazz == null){
//				clazz = entity.getClass();
//			}
//			Field idField = clazz.getDeclaredField( "id" ) ;

			//使用符合JavaBean规范的属性访问器
			PropertyDescriptor pd = new PropertyDescriptor(BaseEntity.ID, clazz);
			//调用setter
//			Method writeMethod = pd.getWriteMethod();    //setName()
//			writeMethod.invoke(obj, "test");
			//调用getter
			Method readMethod = pd.getReadMethod();        //getName()
			Long id = (Long) readMethod.invoke(entity);

//			idField.setAccessible( true );
//			Integer id = (Integer) idField.get(entity);
			if (id == null || id == 0) {
				baseMapper.insert(entity);
			} else {
				baseMapper.update(entity);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} /*catch (NoSuchFieldException e) {
			e.printStackTrace();
		}*/ catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
