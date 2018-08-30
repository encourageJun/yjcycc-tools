package org.yjcycc.tools.common.service;

import org.yjcycc.tools.common.Pager;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface BaseService<T> extends Remote {

	Pager<T> findPager(Map<String, Object> map, int pageNum, int pageSize) throws RemoteException;
	
	T get(T entity) throws RemoteException;
	
	void delete(T entity) throws RemoteException;
	
	void saveOrUpdate(T entity) throws RemoteException;
	
}
