package com.lee.o2o.dao.split;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicDataSourceHolder {

	private static Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);
	private static ThreadLocal<String> contextHolder = new ThreadLocal<String>();
	public static final String DB_MASTER = "master";
	public static final String DB_SLAVE = "slave";

	/**
	 * 获取线程的dbType
	 * @return
	 */
	public static Object getDbType() {
		// 返回对数据库操作的类型
		String db = contextHolder.get();
		if(db == null) {
			db = DB_MASTER;   //====>为什么线程内dbType为空连接主库？？？？？
		}
		return db;
	}

	/**
	 * 设置线程的DBType
	 * @param str
	 */
	public static void setDbType(String str) {
		logger.debug("所使用的数据源为 : " + str);
		contextHolder.set(str);
	}

	/**
	 * 清理连接类型
	 */
	public static void clearDBType() {
		contextHolder.remove();
	}

}
