package com.lee.o2o.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.lee.o2o.exceptions.ShopOperationException;
import com.lee.o2o.BaseTest;
import com.lee.o2o.dto.ImageHolder;
import com.lee.o2o.dto.ShopExecution;
import com.lee.o2o.entity.Area;
import com.lee.o2o.entity.PersonInfo;
import com.lee.o2o.entity.Shop;
import com.lee.o2o.entity.ShopCategory;
import com.lee.o2o.enums.ShopStateEnum;

public class ShopServiceTest extends BaseTest{
	@Autowired
	private ShopService shopService;
	
	@Test
	public void testGetShopList() {
		Shop shopCondition = new Shop();
		ShopCategory sc = new ShopCategory();
		sc.setShopCategoryId(3L);
		shopCondition.setShopCategory(sc);
		ShopExecution se = shopService.getShopList(shopCondition, 2, 3);
		System.out.println("店铺列表数 : " + se.getShopList().size());
		System.out.println("店铺总数 : " + se.getCount());
	}
	
	@Test
	@Ignore
	public void testModifyShop()throws ShopOperationException, FileNotFoundException{
		Shop shop = new Shop();
		shop.setShopId(10L);
		shop.setShopName("修改后店铺名称");
		File shopImg = new File("D://笔记截图/MySql/1.png");
		InputStream is = new FileInputStream(shopImg);
		ImageHolder thumbnail = new ImageHolder("1.png", is);
		ShopExecution shopExecution = shopService.modifyShop(shop, thumbnail);
		System.out.println("新的图片地址:" + shopExecution.getShop().getShopImg());
	}
	
	@Test
	@Ignore
	public void addShopTest() throws FileNotFoundException {
		Shop shop = new Shop();
		PersonInfo owner = new PersonInfo();
		Area area = new Area();
		ShopCategory shopCategory = new ShopCategory();
		owner.setUserId(1L);
		area.setAreaId(2);
		shopCategory.setShopCategoryId(1L);
		shop.setOwner(owner);
		shop.setArea(area);
		shop.setShopCategory(shopCategory);
		shop.setShopName("读写分离service测试");
		shop.setShopAddr("test4");
		shop.setPhone("test4");
//		shop.setShopImg("test1");
		shop.setCreateTime(new Date());
		shop.setEnableStatus(ShopStateEnum.CHECK.getState());
		shop.setAdvice("审核中");
		File shopImg = new File("D://笔记截图/MySql/3.png");
		InputStream is = new FileInputStream(shopImg);
		ImageHolder thumbnail = new ImageHolder(shopImg.getName(), is);
		ShopExecution se = shopService.addShop(shop, thumbnail);
		assertEquals(ShopStateEnum.CHECK.getState(),se.getState());
	}

}
