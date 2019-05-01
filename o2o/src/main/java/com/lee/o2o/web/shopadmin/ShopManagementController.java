package com.lee.o2o.web.shopadmin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.o2o.dto.ImageHolder;
import com.lee.o2o.dto.ShopExecution;
import com.lee.o2o.entity.Area;
import com.lee.o2o.entity.PersonInfo;
import com.lee.o2o.entity.Shop;
import com.lee.o2o.entity.ShopCategory;
import com.lee.o2o.enums.ShopStateEnum;
import com.lee.o2o.exceptions.ShopOperationException;
import com.lee.o2o.service.AreaService;
import com.lee.o2o.service.ShopCategoryService;
import com.lee.o2o.service.ShopService;
import com.lee.o2o.util.CodeUtil;
import com.lee.o2o.util.HttpServletRequestUtil;
import com.lee.o2o.util.ImageUtil;
import com.lee.o2o.util.PathUtil;

@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {
	@Autowired
	private ShopService shopService;

	@Autowired
	private ShopCategoryService shopCategoryService;

	@Autowired
	private AreaService areaService;
	
	@RequestMapping(value="/getshopmanagementinfo", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopManagementInfo(HttpServletRequest request){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		if(shopId <= 0) {
			Object currentShopObj = request.getSession().getAttribute("currentShop");
			if(currentShopObj == null) {
				modelMap.put("redirect", true);
				modelMap.put("url", "/o2o/shopadmin/shoplist");
			}else {
				Shop currentShop = (Shop) currentShopObj;
				modelMap.put("redirect", false);
				modelMap.put("shopId", currentShop.getShopId());
			}
		}else {
			Shop currentShop = new Shop();
			currentShop.setShopId(shopId);
			request.getSession().setAttribute("currentShop", currentShop);
			modelMap.put("redirect", false);
		}
		return modelMap;
	}
	
	@RequestMapping(value = "/getshoplist", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopList(HttpServletRequest request){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//这里先手动添加Session
		PersonInfo user = new PersonInfo();
		user.setUserId(1L);
		user.setName("test");
		request.getSession().setAttribute("user", user);
		user = (PersonInfo)request.getSession().getAttribute("user");
		try {
			Shop shopCondition = new Shop();
			shopCondition.setOwner(user);
			ShopExecution se = shopService.getShopList(shopCondition, 0, 100);
			modelMap.put("shopList", se.getShopList());
			modelMap.put("user", user);
			modelMap.put("success", true);
		}catch(Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		return modelMap;
	}

	
	@RequestMapping(value="/getshopbyid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopById(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		if (shopId > -1) {
			try {
				Shop shop = shopService.getByShopId(shopId);
				List<Area> areaList = areaService.getAreaList();
				modelMap.put("shop", shop);
				modelMap.put("areaList", areaList);
				modelMap.put("success", true);
			} catch (Exception e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty shopId");
		}
		return modelMap;

	}
	
	@RequestMapping(value = "/modifyshop", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		// 1.接受参数并转换相应的参数，包括店铺信息以及图片信息
		/*
		 * 这里需要工具类 1.解析request里的参数
		 */
		String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
		ObjectMapper mapper = new ObjectMapper();
		Shop shop = null;
		try {
			shop = mapper.readValue(shopStr, Shop.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			System.out.println(e.getMessage());
			return modelMap;
		}
		// 处理图片相关逻辑
		CommonsMultipartFile shopImg = null;
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (commonsMultipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
		} 
		// 2.修改店铺信息
		// 首先要判断是否为空
		if (shop != null && shop.getShopId() != null) {
			// 要尽可能少的依赖于前端转过来的信息 eg:例如这个店铺的店主就可以通过会话（session）来获取
			//PersonInfo owner = new PersonInfo();
			// 由于这里还不能冲session中获取 所以就先硬编码
			//修改时就可以重session中获取了
			// session TODO
			//owner.setUserId(1L);
			//shop.setOwner(owner);
			ShopExecution se;
			try {
				if(shopImg == null) {
					se = shopService.modifyShop(shop, null);
				}else {
					ImageHolder thumbnail = new ImageHolder(shopImg.getOriginalFilename(), shopImg.getInputStream());
					se = shopService.modifyShop(shop, thumbnail);
				}
				if (se.getState() == ShopStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("reeMsg", se.getStateInfo());
				}
			} catch (ShopOperationException e) {
				modelMap.put("success", false);
				modelMap.put("reeMsg", e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("reeMsg", e.getMessage());
			}

			return modelMap;
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入店铺信息");
			return modelMap;
		}

	}
	
	
	
	
	

	@RequestMapping(value = "/getshopinitinfo", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopInitInfo() {
		// System.out.println("1111111111");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
		List<Area> areaList = new ArrayList<Area>();
		try {
			shopCategoryList = shopCategoryService.getShopCategoryList(new ShopCategory());
			areaList = areaService.getAreaList();
			modelMap.put("shopCategoryList", shopCategoryList);
			modelMap.put("areaList", areaList);
			modelMap.put("success", true);

		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		// System.out.println("test");
		return modelMap;
	}

	@RequestMapping(value = "/registershop", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> registerShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		// 1.接受参数并转换相应的参数，包括店铺信息以及图片信息
		/*
		 * 这里需要工具类 1.解析request里的参数
		 */
		String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
		ObjectMapper mapper = new ObjectMapper();
		Shop shop = null;
		try {
			shop = mapper.readValue(shopStr, Shop.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
		// 处理图片相关逻辑
		CommonsMultipartFile shopImg = null;
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (commonsMultipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");

		} else {// 如果文件可以为空的话就可以把else的逻辑代码取消掉
			modelMap.put("success", false);
			modelMap.put("errMsg", "上川图片不能为空");
			return modelMap;
		}

		// 2.注册店铺
		// 首先要判断是否为空
		if (shop != null && shopImg != null) {
			// 要尽可能少的依赖于前端转过来的信息 eg:例如这个店铺的店主就可以通过会话（session）来获取
//			PersonInfo owner = new PersonInfo();
			//登录实现后专用session获得
			PersonInfo owner = (PersonInfo) request.getSession().getAttribute("user");
			// 由于这里还不能冲session中获取 所以就先硬编码
			// session TODO
//			owner.setUserId(1L);
			shop.setOwner(owner);
			// 需要传入一个路径 这里先测试传入个空的保证不重复就行
			// File shopImgFile = new File(PathUtil.getImgBasePath() +
			// ImageUtil.getRandomFileName());
			// try {
			// shopImgFile.createNewFile();
			// } catch (IOException e) {
			// modelMap.put("success", false);
			// modelMap.put("errMsg", e.getMessage());
			// return modelMap;
			// }
			// try {
			// inputStreamToFile(shopImg.getInputStream(), shopImgFile);
			// } catch (IOException e) {
			// modelMap.put("success", false);
			// modelMap.put("errMsg", e.getMessage());
			// return modelMap;
			// }
			ShopExecution se;
			try {
				ImageHolder thumbnail = new ImageHolder( shopImg.getOriginalFilename(), shopImg.getInputStream());
				se = shopService.addShop(shop, thumbnail);
				if (se.getState() == ShopStateEnum.CHECK.getState()) {
					modelMap.put("success", true);
					//该用户可以操作的店铺列表
					@SuppressWarnings("unchecked")
					List<Shop> shopList = (List<Shop>) request.getSession().getAttribute("shopList");
					if(shopList == null || shopList.size() == 0) {
						shopList = new ArrayList<Shop>();
					}
					shopList.add(se.getShop());
					request.getSession().setAttribute("shopList", shopList);
				} else {
					modelMap.put("success", false);
					modelMap.put("reeMsg", se.getStateInfo());
				}
			} catch (ShopOperationException e) {
				modelMap.put("success", false);
				modelMap.put("reeMsg", e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("reeMsg", e.getMessage());
			}

			return modelMap;
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入店铺信息");
			return modelMap;
		}

	}

	// private static void inputStreamToFile(InputStream ins, File file) {
	// FileOutputStream os = null;
	// try {
	// os = new FileOutputStream(file);
	// int bytesRead = 0;
	// byte[] buffer = new byte[1024];
	// while ((bytesRead = ins.read(buffer)) != -1) {
	// os.write(buffer, 0, bytesRead);
	// }
	// } catch (Exception e) {
	// throw new RuntimeException("调用inputStreamToFile产生异常" + e.getMessage());
	// } finally {
	// try {
	// if (os != null) {
	// os.close();
	// }
	// if (ins != null) {
	// ins.close();
	// }
	// } catch (IOException e) {
	// throw new RuntimeException("intputStreamToFile关闭io异常" + e.getMessage());
	// }
	// }
	//
	// }

}
