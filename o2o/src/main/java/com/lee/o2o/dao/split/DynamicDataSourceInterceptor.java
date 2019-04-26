package com.lee.o2o.dao.split;

import java.util.Locale;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }) })
public class DynamicDataSourceInterceptor implements Interceptor {
	private static final String REGEX = ".*insert\\u0020.*|.*delete\\u0020.*|.*update\\u0020.*";
	private static Logger logger = LoggerFactory.getLogger(DynamicDataSourceInterceptor.class);

	/**
	 * 在需要拦截的方法上编织代理类去拦截
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		boolean synchronizationActive = TransactionSynchronizationManager.isActualTransactionActive();
		Object[] objects = invocation.getArgs();
		MappedStatement ms = (MappedStatement) objects[0];
		String lookupKey = DynamicDataSourceHolder.DB_MASTER;
		if (synchronizationActive != true) {
			// 读方法
			if (ms.getSqlCommandType().equals(SqlCommandType.SELECT)) {
				// 这是个特例 selectKey 是刚添加shop之后返回自增ID更新图片用的
				if (ms.getId().contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {

					lookupKey = DynamicDataSourceHolder.DB_MASTER;
				} else {
					BoundSql boundSql = ms.getSqlSource().getBoundSql(objects[1]);
					String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replaceAll("[\\t\\n\\r]", " ");
					if (sql.matches(REGEX)) {
						lookupKey = DynamicDataSourceHolder.DB_MASTER;
					} else {
						lookupKey = DynamicDataSourceHolder.DB_SLAVE;
					}
				}
			}
		} else {
			lookupKey = DynamicDataSourceHolder.DB_MASTER;
		}
		logger.debug("设置方法[{}] user [{}] Strateg,SqlCommanType[{}]..", ms.getId(), lookupKey,
				ms.getSqlCommandType().name());
		DynamicDataSourceHolder.setDbType(lookupKey);
		return invocation.proceed();
	}

	/**
	 * 决定返回的时本体类还是代理类
	 */
	@Override
	public Object plugin(Object target) {
		// Executor是mybatis中有关数据库增删改查的类 需要拦截由intercept编织类进行db类型判断
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	/**
	 * 类初始化时做相关配置
	 */
	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}

}
