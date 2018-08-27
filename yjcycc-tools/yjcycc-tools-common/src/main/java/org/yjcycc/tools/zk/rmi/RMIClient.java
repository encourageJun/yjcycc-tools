package org.yjcycc.tools.zk.rmi;

import org.apache.log4j.Logger;
import org.yjcycc.tools.zk.constant.ZkNodeConstant;
import org.yjcycc.tools.zk.client.AbstractZookeeperClient;
import org.yjcycc.tools.zk.model.UsingIpPort;
import org.yjcycc.tools.zk.model.XxNode;
import org.yjcycc.tools.zk.prop.ToolsProperties;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * RMI服务的客户端，用于获取远程服务；<br>
 * 本RMIClient对外通过暴露 getRemoteService 来提供RMI服务；注意，本类的getRemoteService内部是不支持灰度策略的。<br>
 * 注意：如果需要需要灰度策略，建议使用本类的子类 RPCRequest。
 * @author Rosun
 *
 */
public class RMIClient {

	private static final Logger logger = Logger.getLogger(RMIClient.class);

	/**
	 * Key Zookeeper路径（决定了是那一类服务），Value 对应的ZookeeperClient 客户端
	 */
	private static Map<String, AbstractZookeeperClient> nodePathZkcMap = new HashMap<String, AbstractZookeeperClient>();

	/**
	 * RMI超时连接时间 60秒
	 */
	public static final int RMI_TIMEOUT_CONNECT = 60*1000;

	/**
	 * 获取远程服务；适用于RMI接口暴露的所有服务；可以当作本地方法一样访问
	 * 
	 * @param className
	 * @return
	 */
	@SuppressWarnings("unlikely-arg-type")
	public static Object getRemoteService(Class<? extends Remote> className) {
		String serviceName = className.getName();
		
		AbstractZookeeperClient zkclient = getClient(serviceName);
		if (zkclient == null) {
			logger.warn("服务未注册(注意需要用带包名的完整类名来注册RMI服务).请检查服务实现：" + serviceName);
			throw new IllegalStateException("服务未注册(注意需要用带包名的完整类名来注册RMI服务).请检查服务实现：" + serviceName);
		}
		
		//如果不是直接连接服务   调用Zookeeper上的服务；20170521 
		try {
			// 负载均衡 //简单轮询，无路由！（注意  这里无法从上下文获取消费者信息...）
			XxNode producer = zkclient.getOneLivingXxNode();
			if (producer == null) {
//				String zkPath = RMIConfig.getServicePathMap().get(zkclient);
				String zkPath = ZkNodeConstant.BASE_PATH;
				List<XxNode> list = zkclient.readTreeStat(zkPath);
				if (list != null && list.size() > 0) {
					logger.info("本次RMI调用失败，下次调用会ok，请重试. serviceName=" + serviceName);
					zkclient.updateXxNodes(list);
				} else {
					logger.warn("重试后仍然没有找到活着的节点. class=" + zkclient.getClass().getName());
				}
				throw new IllegalStateException("XxZookeeperClient 拿不到一个活着的节点信息:");
			}
			logger.debug("getOneLivingIpPort() --->  no consumer. & producer detail -->" + producer);
			Object service = getProxyedService(null,producer,className);
			return service;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Proxyed service not ready:" + serviceName, e);
		}
	}
	
	protected static AbstractZookeeperClient getClient(final String serviceName) {
		if (nodePathZkcMap.get(ZkNodeConstant.BASE_PATH) != null) {
			return nodePathZkcMap.get(ZkNodeConstant.BASE_PATH);
		}
		AbstractZookeeperClient xzc = new AbstractZookeeperClient() {
			@Override
			public String getNodePathForZk() {
				return ZkNodeConstant.BASE_PATH;
			}
			@Override
			public String getNameSpaceForZK() {
				return ZkNodeConstant.NAME_SPACE;
			}
			@Override
			public String getConnectUrlForZk() {
				return ToolsProperties.getInstance().getZookeeperConnUrl();
			}
			@Override
			public Charset getCharSetForZk() {
				return ZkNodeConstant.CHARSET;
			}
		};
		nodePathZkcMap.put(ZkNodeConstant.BASE_PATH, xzc);
		return xzc;
	}
	
	/**
	 * 获取代理的远程服务对象。在代理对象中会采集方法调用的过程信息。
	 * @param consumer 消费者，请求的发起方，可能为null（为兼容旧的接口）
	 * @param producer 生产者，消息的提供者，不能为null
	 * @param serviceClass 调用的服务Class（注册的服务接口interface的名称）
	 * @return 远程服务对象
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	protected static Object getProxyedService(final XxNode consumer, final XxNode producer , final Class<?extends Remote> serviceClass) throws RemoteException, NotBoundException {
		if(producer == null || serviceClass == null){
			throw new IllegalArgumentException("参数 producer或者serviceName 是 null的");
		}
		//final String serverIp, final int port, 
		final UsingIpPort uip = producer.getUip();
		final int port = uip.getPort();
		final String serverIp = uip.getIp();
		
		Registry registry = RMIRegister.getRegistryClient(serverIp, port, RMI_TIMEOUT_CONNECT);
		Remote rr_service = (Remote) registry.lookup(serviceClass.getName());
		
		final Remote service = rr_service;
		if (service == null) {
			throw new IllegalArgumentException("service name not exist:" + serviceClass.getName());
		}
		
		Object proxyobj = Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if("toString".equals(method.getName())){
					return serviceClass.getName();
				}
				
				Object ret = null;
				try{
					method.setAccessible(true);
					ret = method.invoke(service, args);
				}catch(Exception ex){
					throw ex;
				} finally{
//					jms.sendMessage(rpc.isFailed(), rpc.toString(), null);
				}
				return ret;
			}
		});
		//FIXME 
		return proxyobj;
	}
	
}
