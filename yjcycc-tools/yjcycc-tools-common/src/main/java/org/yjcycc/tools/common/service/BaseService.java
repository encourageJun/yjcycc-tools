package org.yjcycc.tools.common.service;

import org.yjcycc.tools.common.Pager;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface BaseService<T> extends Remote {

	Pager<T> findPager(Map<String, Object> map, int pageNum, int pageSize) throws RemoteException;

	List<T> findList(T t) throws RemoteException;

	List<T> findListByMap(Map<String,Object> map) throws RemoteException;
	
	T get(T entity) throws RemoteException;

	T getById(Long id) throws RemoteException;

	T getByMap(Map<String,Object> map) throws RemoteException;
	
	void delete(T entity) throws RemoteException;
	
	void saveOrUpdate(T entity) throws RemoteException;
	
}
