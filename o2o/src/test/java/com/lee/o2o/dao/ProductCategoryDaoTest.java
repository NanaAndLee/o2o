package com.lee.o2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.lee.o2o.BaseTest;
import com.lee.o2o.entity.ProductCategory;

/**
 *  测试要尽量形成回环   添加 》 查询 》 删除  这样不会对数据库的数据有影响
 *  junit的方法+-执行顺序时随机的  所以要手动指定按顺序执行   1. JVM(对JVM不熟悉 所以就不用这种方法)  2.按照名称顺序@FixMethodOrder(MethodSorters.NAME_ASCENDING)
 * @author 31692
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductCategoryDaoTest extends BaseTest{
	@Autowired
	private ProductCategoryDao productCategoryDao;
	
	@Test
	public void testBQueryByShopId() throws Exception{
		long shopId = 1L;
		List<ProductCategory> productCategoryList = productCategoryDao. queryProductCategoryList(shopId);
		System.out.println("店铺类别个数为 : " + productCategoryList.size());
	}
	
	@Test
	public void testABatchInsertProductCategory() {
		ProductCategory productCategory = new ProductCategory();
		productCategory.setProductCategoryName("商品删除测试1");
		productCategory.setPriority(1);
		productCategory.setCreateTime(new Date());
		productCategory.setShopId(1L);
		ProductCategory productCategory2 = new ProductCategory();
		productCategory2.setProductCategoryName("商品删除测试2");
		productCategory2.setPriority(2);
		productCategory2.setCreateTime(new Date());
		productCategory2.setShopId(1L);
		List<ProductCategory> productCategoryList = new ArrayList<ProductCategory>();
		productCategoryList.add(productCategory);
		productCategoryList.add(productCategory2);
		int effectedNum = productCategoryDao.batchInsertProductCategory(productCategoryList);
		assertEquals(2,effectedNum);
	}
	
	@Test
	public void testCDeleteProductCategory() throws Exception{
		long shopId = 1L;
		List<ProductCategory> productCategoryList = productCategoryDao.queryProductCategoryList(shopId);
		for(ProductCategory pc : productCategoryList) {
			if("商品删除测试1".equals(pc.getProductCategoryName()) || "商品删除测试2".equals(pc.getProductCategoryName())) {
					int effectNum = productCategoryDao.deleteProductCategory(pc.getProductCategoryId(), shopId);
					assertEquals(1, effectNum);
				
			}
		}
	}
}
