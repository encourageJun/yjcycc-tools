package org.yjcycc.tools.zk.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote{

	/**
	 * echo 测试，可判断服务是否可用。结合耗时判断服务端是否忙？
	 * @param world
	 * @return
	 * @throws RemoteException
	 */
	public String echo(String world) throws RemoteException;
	
	/**
	 * 获取服务描述信息 
	 * @return 服务的ip，class，进程id （Map的字符串化表示）
	 * @throws RemoteException
	 */
	public String getServiceDesc()  throws RemoteException;
}
