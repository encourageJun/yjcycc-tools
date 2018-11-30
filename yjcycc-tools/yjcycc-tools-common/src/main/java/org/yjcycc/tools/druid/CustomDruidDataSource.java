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
			String username = ConfigTools.encrypt("biinsertuser");
			String password = ConfigTools.encrypt("9=ktvUC6");
			System.out.println("encrypt: \n" + username + "\n" + password);

			String dUsername = ConfigTools.decrypt("AJSnX8J5NiE0GNvvNyFuZYt6FrcO4hIbPRYuCggR06KkhEivhUxn31vvVRR0VHPpR273XZzcQVCE3xamctOvXQ==");
			String dPassword = ConfigTools.decrypt("HRdK2y2cTqIoU5K1OSs80cR2d1iLSmrH71pUyuLIZWn6V4pdmOrdcOxJ/meb24zcyaLZ34gzfT3dYN3SaSSqIQ==");
			System.out.println("decrypt:\n" + dUsername + "\n" + dPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
