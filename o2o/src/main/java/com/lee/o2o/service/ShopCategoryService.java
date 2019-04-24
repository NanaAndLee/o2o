package com.lee.o2o.service;

import java.util.List;

import com.lee.o2o.entity.ShopCategory;

public interface ShopCategoryService {
	List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition);

}
