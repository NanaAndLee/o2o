package com.lee.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lee.o2o.entity.ProductCategory;
public interface ProductCategoryDao {
	
	/**
	 * 模糊查询 (通过shopId查询店铺类别)
	 * @param shopId
	 * @return
	 */
	List <ProductCategory> queryProductCategoryList(long shopId);
	
	/**
	 * 批量添加商品类别
	 * @param productCategoryList
	 * @return 影响了多上行数
	 */
	int batchInsertProductCategory(List<ProductCategory> productCategoryList);
	
	/**
	 * 删除指定商品类别
	 * @param productCategoryId
	 * @param shopId
	 * @return effectedNum
	 */
	int deleteProductCategory(@Param("productCategoryId") long productCategoryId, @Param("shopId") long shopId);

}
