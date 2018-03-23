package org.yjcycc.tools.zk.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.log4j.Logger;
import org.apache.zookeeper.data.Stat;
import org.yjcycc.tools.common.util.JsonUtil;
import org.yjcycc.tools.common.util.SystemUtil;
import org.yjcycc.tools.zk.model.GrayWhitePubEnum;
import org.yjcycc.tools.zk.model.XxNode;

public abstract class AbstractZookeeperClient {

	private static Logger logger = Logger.getLogger(AbstractZookeeperClient.class);
	
	/**
	 * 在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理
	 */
	private static ExecutorService pool = null;
	
	private CuratorFramework zkTools;

	/**
	 * 节点监听
	 * （1）永久监听指定节点下的节点 
	 * （2）只能监听指定节点下一级节点的变化，比如说指定节点”/example”, 在下面添加”node1”可以监听到，但是添加”node1/n1”就不能被监听到了 
	 * （3）可以监听到的事件：节点创建、节点数据的变化、节点删除等
	 */
	private PathChildrenCache childrenCache;
	
	// 轮询指针
	private int pointer = 0;
	private List<XxNode> xxNodes = new ArrayList<XxNode>();
	private Map<GrayWhitePubEnum, List<XxNode>> xxNodesMap = new HashMap<GrayWhitePubEnum, List<XxNode>>();
	
	/**
	 * 获取远程服务的Zookeeper上的路径（依赖的服务注册在哪个目录下）
	 * @return
	 */
	public abstract String getNodePathForZk();
	
	/**
	 * zookeeper 命名空间
	 * @return
	 */
	public abstract String getNameSpaceForZK();
	
	/**
	 * zookeeper 集群服务链接(例: 127.0.0.1:2181,127.0.0.1:3181,127.0.0.1:4181)
	 * @return
	 */
	public abstract String getConnectUrlForZk();
	
	/**
	 * zookeeper 字符编码
	 * @return
	 */
	public abstract Charset getCharSetForZk();
	
	protected AbstractZookeeperClient() {
		synchronized (xxNodes) {
			boolean result = createTreeNodeObserver();
			if (result) {
				//为解决第一次异步通知事件延迟的问题，尝试等待1秒
				try {
					Thread.sleep(1000);
					//如果这里不等待两秒，则第一次调用Client.getRemoteService 时拿不到活着的节点。
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			} else {
				logger.error("创建监听器 操作失败 ！请检查相关资源是否正确配置及其启动状态是否OK；");
				throw new RuntimeException("创建监听器 操作失败 ！");
			}
		}
	}
	
	/**
	 * 创建观察者 观察节点变化
	 * 
	 * @return
	 */
	private boolean createTreeNodeObserver() {
		if(pool == null){
			pool = Executors.newFixedThreadPool(1);
		}
		// 注册监听 检查节点数据变化
		try {
			final  String connString = getConnectUrlForZk();
			logger.info(">>createTreeNodeObserver :" + connString);
			this.zkTools = createZookeeperClient(connString);
			this.zkTools.start();
			// 初始化的时候加载一次节点数据情况；
			int x = loadNodes();
			logger.info("/初始化的时候加载一次节点数据情况，存活节点数目：" + x + ",存活节点信息：" + this.xxNodes);

			/**
			 * 监听子节点的变化情况
			 */
			childrenCache = new PathChildrenCache(this.zkTools, getNodePathForZk(), true);
			childrenCache.start(StartMode.NORMAL);
			childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
				@Override
				public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
					switch (event.getType()) {
					case CHILD_ADDED:
						logger.info("监听 发生了CHILD_ADDED事件: " + event.getData().getPath()+","+ getNodePathForZk());
						loadNodes();
						break;
					case CHILD_REMOVED:
						logger.info("监听 发生了CHILD_REMOVED事件: " + event.getData().getPath() +","+ getNodePathForZk());
						// FIXME luoshan 通知某节点挂掉了
						// FIXME luoshan 通知某节点挂掉了
						int count = loadNodes();
						if (count == 0) {
							// FIXME luoshan 通知所有节点挂掉了
							logger.info("//FIXME    通知所有节点挂掉了:" + getNodePathForZk());
						}
						break;
					case CHILD_UPDATED:
						logger.info("监听 发生了CHILD_UPDATED事件: " + event.getData().getPath()+","+ getNodePathForZk());
						loadNodes();
						break;
					default:
						break;
					}
				}
			}, pool);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private CuratorFramework createZookeeperClient(String connString) {
		return CuratorFrameworkFactory.builder().connectString(connString)
				.sessionTimeoutMs(2500)
				.connectionTimeoutMs(1500)
				.namespace(getNameSpaceForZK()).retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
				.build();
	}
	
	/**
	 * 从节点树 读取所有的活着的节点的信息。。。。。
	 * 
	 * @return
	 */
	private int loadNodes() {
		xxNodes.clear();
		xxNodesMap.clear();
		List<String> paths;
		try {
			paths = this.zkTools.getChildren().forPath(getNodePathForZk());
			for (String p : paths) {
				logger.info("loadNodes>> p=" + p);
				String ip = new String(this.zkTools.getData().forPath(getNodePathForZk() + "/" + p), getCharSetForZk());
				logger.info("loadNodes>> d=" + ip);
				if (SystemUtil.isIp(ip)) {
					logger.warn("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					logger.warn("ignored get data ：" + getNodePathForZk() + "/" + p + ",d=" + ip);
					logger.warn("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n");
					continue;
				}
				xxNodes.add(JsonUtil.toBean(ip, XxNode.class));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return xxNodes.size();
	}
	
	/**
	 * 轮询获取一个活着的节点，负载均衡. 不区分灰度还是白度，一视同仁
	 * @see com.ihavecar.common.rmi.XxZookeeperClient#getOneLivingIpPort()
	 */
	public XxNode getOneLivingXxNode(){
		int count = xxNodes.size();
		if (count == 0) {
			logger.warn("没有活着的节点了，请检查ZooKeeper上的服务节点注册情况：" + this.getNodePathForZk());
			return null;
		}
		XxNode target = xxNodes.get(pointer % count);
		pointer++;
		if (pointer == 9999) {
			pointer = 0;
		}
		return target;
	}

	/**
	 * 轮询获取一个活着的节点，负载均衡
	 * @param grayWhite 服务节点发布模式选项；
	 * @return
	 */
	public XxNode getOneLivingXxNode(GrayWhitePubEnum grayWhite) {
		int count = xxNodes.size();
		if (count == 0) {
			logger.warn("一个活着的节点都没了，请检查ZooKeeper上的服务节点注册情况：" + this.getNodePathForZk());
			return null;
		}
		List<XxNode> filtedIpPorts = xxNodesMap.get(grayWhite);
		if(filtedIpPorts == null){
			filtedIpPorts = new ArrayList<XxNode>();
			//从ipPorts中挑选出符合 grayWhite 条件的节点来
			for(XxNode node : xxNodes){
				if(node.getGrayWhiteMode().equals(grayWhite)){
					filtedIpPorts.add(node);
				}
			}
			xxNodesMap.put(grayWhite, filtedIpPorts);
		}
		count = filtedIpPorts.size();
		if (count == 0) {
			logger.warn("没有活着的【"+grayWhite+"】模式的节点了，请检查ZooKeeper上的服务节点注册情况：" + this.getNodePathForZk());
			return null;
		}
		//负载均衡，轮询
		XxNode target = filtedIpPorts.get(pointer % count);
		//FIXME 挑选出指定的模式的（白度？灰度 ？还是其他自定义分组的？）
		pointer++;
		if (pointer == 99999) {
			pointer = 0;
		}
		return target;
	}
	
	/**
	 * 刷新节点信息（通常是由ZK观察者收到数据变更通知后）
	 * @param list
	 */
	public void updateXxNodes(List<XxNode> list) {
		logger.info("updateIpPorts,list.size="+list.size());
		this.xxNodes = list;
		this.xxNodesMap.clear();
	}
	
	/**
	 * 读取节点信息 （非新建客户端）
	 * 
	 * @return
	 */
	public List<XxNode> readTreeStat(final String zookeeperPath) {
		CuratorFramework client = null;
		List<XxNode> tmpIpPorts = new ArrayList<>();
		try {
			String connString = getNodePathForZk();
			client = createZookeeperClient(connString);
			client.start();
			Stat stat = client.checkExists().forPath(zookeeperPath);
			if(stat == null || stat.getNumChildren() == 0){
				logger.warn("没有子节点");
				return tmpIpPorts;
			}
			int children = stat.getNumChildren();
			logger.info("readTreeStat >>> getNumChildren=" + children + ",stat=" + stat);
			List<String> paths;
			try {
				paths = client.getChildren().forPath(zookeeperPath);
				for (String p : paths) {
					logger.info("loadNodes>> p=" + p);
					String d = new String(client.getData().forPath(zookeeperPath + "/" + p), getCharSetForZk());
					logger.info("loadNodes>> d=" + d);
					if (SystemUtil.isIp(d)) {
						logger.warn("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
						logger.warn("ignored get data ：" + zookeeperPath + "/" + p + ",d=" + d);
						logger.warn("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n");
						continue;
					}
					tmpIpPorts.add(JsonUtil.toBean(d, XxNode.class));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(client != null){
				client.close();
			}
		}
		return tmpIpPorts;
	}
	
	/**
	 * 关闭Zk客户端，释放长连接
	 */
	public void stop() {
		try {
			if (this.zkTools != null) {
				this.zkTools.close();
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		if (pool != null) {
			pool.shutdown();
		}
		if (childrenCache != null) {
			try {
				childrenCache.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((xxNodes == null) ? 0 : xxNodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractZookeeperClient other = (AbstractZookeeperClient) obj;
		if (getNodePathForZk() == null) {
			if (other.getNodePathForZk() != null)
				return false;
		} else if (!getNodePathForZk().equals(other.getNodePathForZk()))
			return false;
		return true;
	}
}
