package com.lee.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lee.o2o.entity.Shop;

public interface ShopDao {

	/**
	 * 分页查询店铺，可输入条件如：店铺名(模糊查询) 店铺状态 区域Id owner
	 * @Param : 当有多个参数时来指定各个参数标识 根据参数标识进行数据的读取
	 * @param shopCondition
	 * @param rowIndex 从第几行开始取
	 * @param pageSize 返回的条数
	 * @return
	 */
	List<Shop> queryShopList(@Param("shopCondition") Shop shopCondition, @Param("rowIndex") int rowIndex,
			@Param("pageSize") int pageSize);

	/**
	 * 返回queryShopList总数
	 * @param shopCondition
	 * @return
	 */
	int queryShopCount(@Param("shopCondition") Shop shopCondition);
	
	/**
	 * 通过shop id 查询店铺
	 * 
	 * @param shopId
	 * @return
	 */
	Shop queryByShopId(long shopId);

	/**
	 * 新增店铺
	 * 
	 * @param shop
	 * @return
	 */
	int insertShop(Shop shop);

	/**
	 * 更新店铺信息
	 * 
	 * @param shop
	 * @return
	 */
	int updateShop(Shop shop);

}
