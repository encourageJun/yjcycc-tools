package org.yjcycc.tools.fastdfs;

import org.csource.fastdfs.ClientGlobal;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class FdfsConnectionPoolServiceImpl implements FdfsConnectionPoolService {
	private String configFileName = "fdfs_client.properties";

	public FdfsConnectionPoolServiceImpl(){
		super();
	}
	//初始化参数
	@PostConstruct
	public void init() throws Exception {
		Resource res = new ClassPathResource(configFileName);
        ClientGlobal.init(res.getFile().getAbsolutePath());
	}
	//获取链接
	public FdfsManagerConnection getFdfsConnection() throws Exception{

		FdfsManagerConnectionManager manager = FdfsManagerConnectionManager.getInstance();
		FdfsManagerConnection conn = manager.getFdfsManagerConnection();
		return conn;
	}

	//置为空闲
	public void releaseFdfsConnection(FdfsManagerConnection conn){
		FdfsManagerConnectionManager manager = FdfsManagerConnectionManager.getInstance();
		manager.releaseManagerFdfsConnection(conn);
	}
}
