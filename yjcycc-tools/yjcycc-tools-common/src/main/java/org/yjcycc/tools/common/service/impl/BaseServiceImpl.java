package org.yjcycc.tools.common.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.yjcycc.tools.common.Pager;
import org.yjcycc.tools.common.mapper.MyBatisBaseMapper;
import org.yjcycc.tools.common.service.BaseService;
import org.yjcycc.tools.common.util.PagerUtil;

import java.lang.reflect.Field;
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
			Field idField = entity.getClass().getDeclaredField( "id" ) ;
			idField.setAccessible( true );
			Integer id = (Integer) idField.get(entity);
			if (id == null || id == 0) {
				baseMapper.insert(entity);
			} else {
				baseMapper.update(entity);
			}
			
			//创建类
			/*Class<?> class1 = Class.forName("com.app.Person");

            //取得本类的全部属性
            Field[] fields = entity.getClass().getDeclaredFields();

            for (Field field : fields) {
                System.out.println( field );
                //打印 person 的属性值
                System.out.println( field.get( person ));
            }*/
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
