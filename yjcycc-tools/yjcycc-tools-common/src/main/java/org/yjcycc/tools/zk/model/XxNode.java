package org.yjcycc.tools.zk.model;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.yjcycc.tools.common.util.JsonUtil;

/**
 * 服务化节点；<br>
 * 1个派单引擎是一个节点：服务节点，支付服务，票据服务，订单服务，魔镜服务，GIS服务等是目前已经存在的服务；
 * 1个司机端Tomcat 或者顺风车乘客段也是一个节点：消费者节点
 * @author Rosun
 *
 */
public class XxNode implements Serializable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5990235277093339096L;

	private String nodeId;

	public XxNode(){
		this.createTime = new Date();
		this.grayWhiteMode = GrayWhitePubEnum.GRAY;
		this.nodeId = UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public void copy(XxNode other){
		this.envriment = other.getEnvriment();
		this.grayWhiteMode = other.getGrayWhiteMode();
		this.name = other.getName();
		this.pcMode = other.getPcMode();
		this.uip = other.getUip();
	}
	 
	
	
	/**
	 * 当前环境
	 */
	private String envriment;
	
	/**
	 * 节点名称
	 */
	private String name;
	
	/**
	 * 节点创建时间
	 */
	private Date createTime;
	
	/**
	 * 使用ip 端口，序号，
	 */
	private UsingIpPort uip;
	
	/**
	 * 发布 模式；是否灰度? 还是白度？
	 */
	private GrayWhitePubEnum grayWhiteMode;
	
	/**
	 * 生产者还是消费者
	 */
	private ProducerConsumerEnum pcMode;
	
	

	public GrayWhitePubEnum getGrayWhiteMode() {
		return grayWhiteMode;
	}

	public void setGrayWhiteMode(GrayWhitePubEnum grayWhiteMode) {
		this.grayWhiteMode = grayWhiteMode;
	}

	public UsingIpPort getUip() {
		return uip;
	}

	public void setUip(UsingIpPort uip) {
		this.uip = uip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getEnvriment() {
		return envriment;
	}

	public void setEnvriment(String envriment) {
		this.envriment = envriment;
	}
	
	
	public String toString(){
		return JsonUtil.toJson(this);
	}

	public ProducerConsumerEnum getPcMode() {
		return pcMode;
	}

	public void setPcMode(ProducerConsumerEnum pcMode) {
		this.pcMode = pcMode;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
}
