package org.yjcycc.tools.zk.server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;
import org.yjcycc.tools.common.util.JsonUtil;
import org.yjcycc.tools.zk.model.XxNode;

@Component
public abstract class AbstractZookeeperClientRegister {
	
	protected static Logger logger = Logger.getLogger(AbstractZookeeperClientRegister.class);
	
	private static long index_lost = 0;
	private static long index_rec = 0;
	
	private transient boolean isRun = false; 
	
	private XxNode xxNode = null;
	
	private CuratorFramework zkTools;
	
	private PathChildrenCache childrenCache ;
	
	/**
	 * 在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理
	 */
	private static ExecutorService pool = null;
	
	/**
	 * zookeeper 命名空间
	 * @return
	 */
	public abstract String getNameSpaceForZK();
	
	/**
	 * zookeeper 节点路径
	 * @return
	 */
	public abstract String getNodePathForZk();
	
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
	
	public boolean isRunning() {
		return isRun;
	}
	
	@PreDestroy
	public void stop() {
		try{
			if(zkTools != null) {
				zkTools.close();
			}
		} catch(Exception ex) {
			logger.error(ex.getMessage(),ex);
		}
		
		if(pool != null){
			pool.shutdown();
		}
		if(childrenCache != null){
			try {
				childrenCache.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		isRun = false;
	}

	private CuratorFramework createZookeeperClient(String connString) {
		return CuratorFrameworkFactory.builder().connectString(connString)
				.sessionTimeoutMs(2500)
				.connectionTimeoutMs(1500)
				.namespace(getNameSpaceForZK()).retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
				.build();
	}
	
	/**
	 * 创建Zookeeper 客户端  并创建临时树节点
	 * @param usingIpPort
	 * @return
	 */
	public boolean createTreeNode(XxNode usingIpPort) throws Exception {
		this.xxNode = usingIpPort;
		if (this.xxNode == null) {
			throw new IllegalStateException("ERROR ! parameter xxNode not setted yet!");
		}
		String connString = getConnectUrlForZk();
		logger.info("开始链接Zookeeper咯~        xxNode="+this.xxNode.toString() + ",Zookeeper connString="+connString);
		
		//创建客户端
		this.zkTools = createZookeeperClient(connString);
		this.zkTools.start();
		
		//modify luoshan 20170524 采用pid 代替 index  注意节点路径变化了，不再是 0 1 2 
		final String dataPath = getNodePathForZk() +"/"+ this.xxNode.getUip().getPid();
		logger.info("zkTools.start ok !   dataPath="+ dataPath);
		try {			
			Stat exist = zkTools.checkExists().forPath(getNodePathForZk());
			if(exist == null){
				logger.info("------  第一次构建basepath:" + getNodePathForZk());
				zkTools.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
				.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(getNodePathForZk());
			}
			
			// 创建节点路径
			zkTools.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
					.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(dataPath);
			
			logger.info("\n\n\n\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 对树路径节点赋值 start");
			logger.info("%%%%%%%%%    dataPath=" + dataPath + " @namespace="+zkTools.getNamespace());
			
			// 对路径节点赋值
			final byte[] data = this.xxNode.toString().getBytes(getCharSetForZk());// 节点值
			zkTools.setData().forPath(dataPath, data);
			logger.info("%%%%%%%%%    data=" + new String(data));
			
			isRun = true;
			
			logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 对树路径节点赋值 end");
		} catch (Exception e) {
			e.printStackTrace();
			isRun = false;
		} 
		
		//防止节点 闪断后数据丢失，增加监听 20170527 罗珊
		this.zkTools.getConnectionStateListenable().addListener(getConnectionStateListener(dataPath, this.xxNode));
		logger.info("%%%%%%%%% 对树路径节点 增加闪断监听器，兼容网络错误 :" + dataPath);
		//防止节点 闪断后数据丢失，增加监听 20170527 罗珊
		
		//创建观察者 观察自身节点变化（以便重连时保持数据连续性）
		createTreeNodeObserver(dataPath);		
		logger.info("%%%%%%%%% 创建观察者 观察自身节点变化（以便重连时保持数据连续性） :" + dataPath);
		logger.info(" XxCarZookeeperClient start >>> " + isRun);
		
		return this.isRun;
	}
	
	/**
	 * 闪断监听, 闪断后重写节点数据
	 * @param zkRegPathPrefix
	 * @param regContent
	 * @return
	 */
	private ConnectionStateListener getConnectionStateListener(final String zkRegPathPrefix, final XxNode regContent) {
		return new ConnectionStateListener() {
			@Override
			public void stateChanged(CuratorFramework zkTools, ConnectionState connectionState) {
				if(connectionState == ConnectionState.LOST){
					logger.warn("EEEEE ：ConnectionState.LOST ，Zookeeper连接断开啦，请检查网络");
					return;
				}
				if(connectionState == ConnectionState.RECONNECTED){
					try{
						//如果是重新连上了。检查节点路径是不是没数据了，
						Stat exist = zkTools.checkExists().forPath(zkRegPathPrefix);
						if(exist == null){
							logger.warn("EEEEE ：ConnectionState.RECONNECTED ，断开重连了，数据不在，准备重写:");
							//断开了 ，数据不在了，需要重新写临时数据
							final String d = regContent.toString();
							zkTools.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
									.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(zkRegPathPrefix);
							zkTools.setData().forPath(zkRegPathPrefix, d.getBytes(getCharSetForZk()));
							logger.info("EEEEE ：ConnectionState.RECONNECTED ，断开重连了，数据不在，重写OK!");
						}else{
							//断开了，数据还在，不用写临时数据；
							logger.warn("EEEEE ：ConnectionState.RECONNECTED ，断开重连了，数据还在;");
						}
					}catch(Exception ex){
						logger.error("Exception:" + ex, ex);
					}
				}
			}
		};
	}
	
	/**
	 * 创建观察者 观察自身节点变化（以便重连时保持数据连续性）
	 * @return
	 */
	private boolean createTreeNodeObserver(final String dataPath) {
		if(pool == null){
			pool = Executors.newFixedThreadPool(1);
		}
		if(this.zkTools == null){
			String connString = getNodePathForZk();
			this.zkTools = this.createZookeeperClient(connString);
			this.zkTools.start();
		}
		//注册监听   检查节点数据变化
		try {
			byte[] buffer = zkTools.getData().forPath(getNodePathForZk() +"/"+ xxNode.getUip().getPid());
			logger.info("createTreeNodeObserver >>> 获取数据 ：  "+getNodePathForZk() + "/"+xxNode.getUip().getPid()+" => " +new String(buffer));
			
			/**
			 * 监听子节点的变化情况
			 */
			childrenCache = new PathChildrenCache(zkTools,  getNodePathForZk(), true);
			childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
			childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
				@Override
				public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
					switch (event.getType()) {
					case CHILD_ADDED:
						logger.info("监听: 发生了CHILD_ADDED事件: " + event.getData().getPath());
						zookeeperChildAdd(event.getData().getPath(),event);
						break;
					case CHILD_REMOVED:
						logger.debug("监听: 发生了CHILD_REMOVED事件: " + event.getData().getPath());
						zookeeperChildRemove(event.getData().getPath(),event);
						break;
					case CHILD_UPDATED:
						logger.debug("监听: 发生了CHILD_UPDATED事件:" + event.getData().getPath());
						zookeeperChildUpdate(event.getData().getPath(),event);
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
	
	/**
	 * 兄弟节点增加了
	 * @param path 事件节点的路径
	 * @param event 事件对象（包含事件中的数据）
	 */
	public void zookeeperChildAdd(String path, PathChildrenCacheEvent event) {
		//默认实现， 
		final String meDataPath = getNodePathForZk() +"/"+ this.xxNode.getUip().getPid();
		//默认实现，如果是自己更新了，将更新后的状态记录下来。 
		//观察自身节点变化,这种变化通常来自星星之眼，（以便重连时保持数据连续性）
		if(path.equals(meDataPath)){
			if(index_rec == 999999999){
				index_rec = 0;
			}else{
				index_rec ++;
			}
			// FIXME 监听到网络重连(做些事情)
			
			//触发网络断线或重连事件   20171117 罗珊
//			NetConnectionDTO dto = new NetConnectionDTO(NetConnectionDTO.TYPE_REC, index_rec, this.xxNode);
//			NetConnectionQueue.happenNetConnection(dto);
			//触发网络断线或重连事件   20171117 罗珊
		}
	}
	
	/**
	 * 兄弟节点挂了
	 * @param path 事件节点的路径
	 * @param event 事件对象（包含事件中的数据）
	 */
	public void zookeeperChildRemove(String path, PathChildrenCacheEvent event) {
		//默认实现， 
		final String meDataPath = getNodePathForZk() +"/"+ this.xxNode.getUip().getPid();
		//默认实现，如果是自己更新了，将更新后的状态记录下来。 
		//观察自身节点变化,这种变化通常来自星星之眼，（以便重连时保持数据连续性）
		if(path.equals(meDataPath)){
			//触发网络断线或重连事件   20171117 罗珊
			if(index_lost == 999999999){
				index_lost = 0;
			}else{
				index_lost ++;
			}
			// FIXME 监听到网络断开就发送通知
			
//			NetConnectionDTO dto = new NetConnectionDTO(NetConnectionDTO.TYPE_DIS, index_lost, this.xxNode);
//			NetConnectionQueue.happenNetConnection(dto);
			//触发网络断线或重连事件   20171117 罗珊
		}
		
		
	}
	
	/**
	 * 兄弟节点更新了
	 * @param path 事件节点的路径
	 * @param event 事件对象（包含事件中的数据）
	 */
	public void zookeeperChildUpdate(String path, PathChildrenCacheEvent event) {
		final String meDataPath = getNodePathForZk() +"/"+ this.xxNode.getUip().getPid();
		//默认实现，如果是自己更新了，将更新后的状态记录下来。 
		//观察自身节点变化,这种变化通常来自星星之眼，（以便重连时保持数据连续性）
		if(path.equals(meDataPath)){
			String d = null;
			try {
				d = new String(zkTools.getData().forPath(meDataPath), getCharSetForZk());
			} catch (Exception e) {
				e.printStackTrace();
			}
			XxNode updatedNode = JsonUtil.toBean(d, XxNode.class);
			xxNode.copy(updatedNode);
			logger.info("服务提供者节点更新，After updated:" + xxNode);
		}
	}
	
}
