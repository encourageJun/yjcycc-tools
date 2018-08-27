package org.yjcycc.tools.druid;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * @description 自定义数据源连接池，扩展DruidDataSource连接池
 * @author biao
 * @date 2017年1月9日
 */
@SuppressWarnings("all")
public class CustomDruidDataSource extends DruidDataSource {
	
	@Override
	public void setUsername(String username) {
		try {
			username = ConfigTools.decrypt(username);
		} catch (Exception e) {
			throw new RuntimeException("Decrypt Database userName exception.");
		}
		super.setUsername(username);
    }
	
	@Override
	public void setPassword(String password) {
		try {
			password = ConfigTools.decrypt(password);
		} catch (Exception e) {
			throw new RuntimeException("Decrypt Database password exception.");
		}
        super.setPassword(password);
    }
	
	public static void main(String[] args) {
		try {
			String username = ConfigTools.encrypt("yjcycc");
			String password = ConfigTools.encrypt("Yjcycc123");
			System.out.println(username + "\n" + password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
