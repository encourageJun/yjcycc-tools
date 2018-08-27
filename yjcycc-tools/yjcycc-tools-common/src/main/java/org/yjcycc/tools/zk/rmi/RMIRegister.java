package org.yjcycc.tools.zk.rmi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.AccessException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;

/**
 * RMI注册机  统一RMI注册过程
 */
public class RMIRegister {
	
	private static Logger logger = Logger.getLogger(RMIRegister.class);
	
	private Registry registry = null; 
	
	/**
	 * RMI注册构造器
	 * @param port RMI服务端口（防止被占用，否则会报错）
	 * @throws RemoteException
	 */
	public RMIRegister(int port) throws RemoteException{
		this.registry = LocateRegistry.createRegistry(port);
	}
	
	/**
	 * 注册RMI 服务；
	 * @param serviceClass 使用class name作为注册在RMI中的KEY
	 * @param bean 真实的远程服务实现类
	 * @return 注册器本身
	 * @throws AccessException
	 * @throws RemoteException
	 */
	public RMIRegister regist(Class<? extends Remote> serviceClass, Remote bean) throws AccessException, RemoteException{
		Remote rmiService = (Remote) UnicastRemoteObject.exportObject(bean, 0);
		registry.rebind(serviceClass.getName(), rmiService);
		logger.info("regist : " + serviceClass.getName());
		return this;
	}
	
	/**
	 * 获取RMI Client
	 * @param serverIp
	 * @param port
	 * @param connetTimeOut
	 * @return
	 * @throws RemoteException
	 */
	public static Registry getRegistryClient(String serverIp, int port, final int connetTimeOut) throws RemoteException {
		return LocateRegistry.getRegistry(serverIp, port, new RMIClientSocketFactory() {
			@Override
			public Socket createSocket(String host, int port) throws IOException {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(host, port), connetTimeOut);
				return socket;
			}
		});
	}
}
