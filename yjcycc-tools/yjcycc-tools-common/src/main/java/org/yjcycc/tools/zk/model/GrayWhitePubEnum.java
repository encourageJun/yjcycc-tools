package org.yjcycc.tools.zk.model;

/**
 * 灰度发布？？还是正常发布？   模式枚举
 * @author Rosun
 *
 */
public enum GrayWhitePubEnum {
	
	/**
	 * 不区分分组；建议用在单节点的资源的场合（资源池）；
	 */
	NO_DISTINGUISH_0(0),
	
	/**
	 * 扩展分组101 预留；不建议测试环境和线上环境使用；建议用在开发调试环境；
	 */
	FOR_EXTEND_101(101),
	 

	/**
	 * 是灰度发布；建议用在测试或者线上环境；
	 */
	GRAY(50),
	
	/**
	 * 正常发布；建议用在测试或者线上环境；
	 */
	WHITE(100);
	
	
	
	
	
	
	private int value;
	
	GrayWhitePubEnum(int value){
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}
