package com.lee.o2o.util;

public class PathUtil {
	private static String seperator = System.getProperty("file.separator");

	/**
	 * 
	 * @return  项目图片根路径
	 */
	public static String getImgBasePath() {
//		String os = System.getenv("os.name");
		String os = System.getProperty("os.name");
		String basePath = "";
		if (os.toLowerCase().startsWith("win")) {
			basePath = "D:/project/o2o/image/";
		} else {
			basePath = "User/o2o/work";
		}
		basePath = basePath.replace("/", seperator);
		return basePath;
	}

	
	/**
	 * 
	 * @param shopId
	 * @return 项目图片的子路径
	 */
	public static String getShopImagePath(long shopId) {
		String imagePath = "upload/item/shop/" + shopId + "/";
		return imagePath.replace("/", seperator);
	}
}
