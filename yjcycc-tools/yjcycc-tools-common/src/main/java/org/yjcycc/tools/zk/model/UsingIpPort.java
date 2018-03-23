package org.yjcycc.tools.zk.model;

import java.io.Serializable;

public class UsingIpPort implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5431929997479219977L;

	/**
	 * 服务器ip
	 */
	private String ip;

	/**
	 * 端口
	 */
	private int port;

	/**
	 * 用的第几套配置文件；
	 */
	private int pid;
	
	public UsingIpPort() {}
	
	public UsingIpPort(String ip, int port, int pid) {
		this.ip = ip;
		this.port = port;
		this.pid = pid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}
	
	/** 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pid;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + port;
		return result;
	}

	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsingIpPort other = (UsingIpPort) obj;
		if (pid != other.pid)
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
	
}
