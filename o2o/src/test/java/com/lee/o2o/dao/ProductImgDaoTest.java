package com.lee.o2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.lee.o2o.BaseTest;
import com.lee.o2o.entity.ProductImg;

/**
 * 建立回环测试
 * @author 31692
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductImgDaoTest extends BaseTest{
	@Autowired
	private ProductImgDao productImgDao;
	
	@Test
	public void testABatchInsertProductImg() throws Exception{
		//为productId 为1的商品添加两个详情图片记录
		ProductImg productImg1 = new ProductImg();
		productImg1.setImgAddr("图片1");
		productImg1.setImgDesc("测试图片1");
		productImg1.setPriority(1);
		productImg1.setCreateTime(new Date());
		productImg1.setProductId(3L);
		
		ProductImg productImg2 = new ProductImg();
		productImg2.setImgAddr("图片2");
		productImg2.setImgDesc("测试图片2");
		productImg2.setPriority(1);
		productImg2.setCreateTime(new Date());
		productImg2.setProductId(3L);
		List<ProductImg> productImgList = new ArrayList<ProductImg>();
		productImgList.add(productImg1);
		productImgList.add(productImg2);
			int effectedNum = productImgDao.batchInsertProductImg(productImgList);
			assertEquals(2, effectedNum);

	}
	
	@Test
	public void testCDeleteProductImgByProductId() throws Exception{
		//删除该商品下的所有详情图片记录
		long productId = 3;
		int effectedNum = productImgDao.deleteProductImgByProductId(productId);
		assertEquals(7,effectedNum);
	}
	

}
