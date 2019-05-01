package com.lee.o2o.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lee.o2o.dao.ProductDao;
import com.lee.o2o.dao.ProductImgDao;
import com.lee.o2o.dto.ImageHolder;
import com.lee.o2o.dto.ProductExecution;
import com.lee.o2o.entity.Product;
import com.lee.o2o.entity.ProductImg;
import com.lee.o2o.enums.ProductStateEnum;
import com.lee.o2o.exceptions.ProductOperationException;
import com.lee.o2o.service.ProductService;
import com.lee.o2o.util.ImageUtil;
import com.lee.o2o.util.PageCalculator;
import com.lee.o2o.util.PathUtil;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductDao productDao;

	@Autowired
	private ProductImgDao productImgDao;

	@Override
	@Transactional
	public ProductExecution addProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgHolderList)
			throws ProductOperationException {
		//1.处理缩略图, 获取缩略图相对路径并赋值给product
		//2. 往tb_product写入商品信息,获取productId
		//3. 结合productId批量处理商品信息详情图
		//4. 将商品详情图列表批量插入tb_product_img中
		
		//控制判断
		if(product != null && product.getShop() != null && product.getShop().getShopId() != null) {
			//给商品设置上默认属性
			product.setCreateTime(new Date());
			product.setLastEditTime(new Date());
			//默认上架状态
			product.setEnableStatus(1);
			//若商品缩略图不为空添加
			if(thumbnail != null) {
				addThumbnail(product, thumbnail);
			}
			try {
				//创建商品信息
				int effectedNum = productDao.insertProduct(product);
				if(effectedNum <= 0) {
					throw new ProductOperationException("创建商品失败");
				}
				
			} catch(ProductOperationException e) {
				throw new ProductOperationException("创建商品失败:" + e.getMessage());
			}
			//若商品详情图不为空则添加
			if(productImgHolderList != null && productImgHolderList.size() > 0) {
				addProductImgList(product, productImgHolderList);
			}
			return new ProductExecution(ProductStateEnum.SUCCESS, product);
		}else {
			//传入为空则返回错空置误信息
			return new ProductExecution(ProductStateEnum.EMPTY);
		}
	}

	
	@Override
	public Product getProductById(long productId) {
		return productDao.queryProductById(productId);
	}


	@Override
	@Transactional
	public ProductExecution modifyProduct(Product product, ImageHolder thumbnail,
			List<ImageHolder> productImgHolderList) throws ProductOperationException {
		//1.若缩略图参数有值，则处理缩图 若原先存在缩略图则先删除在添加新的缩略图，之后再获取缩略图的相对路径再赋值给product
		//2.若商品详情图列表参数有值，对商品详情图片列表进行同样的操作
		//3.将tb_product_img下面的该商品图片原先的商品详情图片记录全部清除
		//3.更新tb_product以及tb_product_img的信息
		
		//空置判断
		if(product != null && product.getShop() != null && product.getShop().getShopId() != null) {
			//给商品设置默认属性
			product.setLastEditTime(new Date());
			//若缩略图不为空且原有缩略图不为空则删除原有缩略图片并添加
			if(thumbnail != null) {
				//先获取一边原有的信息，因为原来的信息里有图片地址
				Product tempProduct = productDao.queryProductById(product.getProductId());
				if(tempProduct.getImgAddr() != null) {
					ImageUtil.deleteFileOrPath(tempProduct.getImgAddr());
				}
				addThumbnail(product, thumbnail);
			}
			//如果有新存入的商品详情图，则将原先的删除，并添加新的图片
			if(productImgHolderList != null && productImgHolderList.size() > 0) {
				deleteProductImgList(product.getProductId());
				addProductImgList(product, productImgHolderList);
			}
			try {
				//更新商品信息
				int effectedNum = productDao.updateProduct(product);
				if(effectedNum <= 0) {
					throw new ProductOperationException("更新商品信息失败");
				}
				return new ProductExecution(ProductStateEnum.SUCCESS, product);
			}catch(Exception e) {
				throw new ProductOperationException("更新商品信息失败:" + e.toString());
			}
		}else {
			return new ProductExecution(ProductStateEnum.EMPTY);
		}
	}

	
	
	
	
	/**
	 * 添加缩略图
	 * @param product
	 * @param thumbnail
	 */
	private void addThumbnail(Product product, ImageHolder thumbnail) {
		String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
		String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail, dest);
		product.setImgAddr(thumbnailAddr);
		
	}

	/**
	 * 批量添加图片
	 * @param product
	 * @param productImgHolderList
	 */
	private void addProductImgList(Product product, List<ImageHolder> productImgHolderList) {
		//获取图片存储路径，这里直接存放到相应店铺的文件夹地下
		String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
		List<ProductImg> productImgList = new ArrayList<ProductImg>();
		//遍历图片依次处理，并添加productImg实体类里
		for(ImageHolder productImgHolder : productImgHolderList) {
			String imgAddr = ImageUtil.generateNormalImg(productImgHolder, dest);
			ProductImg productImg = new ProductImg();
			productImg.setImgAddr(imgAddr);
			productImg.setProductId(product.getProductId());
			productImg.setCreateTime(new Date());
			productImgList.add(productImg);
		}
		//如果确实是有图片需要添加的，就执行批量添加操作
		if(productImgList.size() > 0) {
			try {
				int effectedNum = productImgDao.batchInsertProductImg(productImgList);
				if(effectedNum <= 0) {
					throw new ProductOperationException("创建商品详情图片失败");
				}
			}catch(Exception e) {
				throw new ProductOperationException("创建商品详情图片失败:"+e.toString());
			}
		}
		
	}


	/**
	 * 删除某个商品下的所有详情图
	 * @param productId
	 */
	private void deleteProductImgList(long productId) {
		//根据productId获取原来的图片
		List<ProductImg> productImgList = productImgDao.queryProductImgList(productId);
		//删除原来的图片
		for(ProductImg productImg : productImgList) {
			ImageUtil.deleteFileOrPath(productImg.getImgAddr());
		}
		//删除数据库原有的图片的记录
		productImgDao.deleteProductImgByProductId(productId);
	}


	@Override
	public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize) {
		//页码转换成数据库的行码，并调用dao层取回指定页码的商品列表
		int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		List<Product> productList = productDao.queryProductList(productCondition, rowIndex, pageSize);
		//基于同样的查询条件返回该查询条件下的沙商品总数
		int count = productDao.queryProductCount(productCondition);
		ProductExecution pe = new ProductExecution();
		pe.setProductList(productList);
		pe.setCount(count);
		return pe;
	}


}
