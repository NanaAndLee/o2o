package com.lee.o2o.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.lee.o2o.dto.ImageHolder;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

public class ImageUtil {
	// public static void main(String[] args) throws IOException {
	// String basePath =
	// Thread.currentThread().getContextClassLoader().getResource("").getPath();
	// Thumbnails.of(new File("D://笔记截图/o2o/before.png")).size(200, 200)
	// .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath +
	// "/test.png")), 0.25f)
	// .outputQuality(0.8f).toFile("D://笔记截图/o2o/lasttest.png");
	// }
	
//	private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	private static String basePath = "D://eclipse-workspace/o2o/src/main/resources";
	private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final Random r = new Random();
	private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
	
	/**
	 * 将CommonsMultiparyFile转换成File类
	 * @param cFile
	 * @return
	 */
	public static File transferCommonsMultiparyFileToFile(CommonsMultipartFile cFile) {
		File newFile = new File(cFile.getOriginalFilename());
		try {
			cFile.transferTo(newFile);
		} catch (IllegalStateException e) {
			logger.error(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		return newFile;
	}
	/**
	 * 处理缩略图,并返回新生成图片得相对值路径
	 * @param thumbnail
	 * @param targetAddr
	 * @return
	 */
	public static String generateThumbnail(ImageHolder thumbnail, String targetAddr) {
		//获取不重复的随机名
		String realFileName = getRandomFileName();
		//获取文件的扩展名
		String extension = getFileExtension(thumbnail.getImageName());
		//如果目标路径不存在，则自动创建
		makeDirPath(targetAddr);
		//获取文件存储的相对路径(带文件名)
		String relativeAddr = targetAddr + realFileName + extension;
		logger.debug("current relativeAddr is:"+relativeAddr);
		//获取文件要保存到的目标路径
		File dest = new File(PathUtil.getImgBasePath() + relativeAddr);
		logger.debug("current complete addr is:"+ PathUtil.getImgBasePath() + relativeAddr);
		//调用Thumbnails生成带有水印的图片
		try {
			// 该缩略图要用到很多次，所以可以定义为静态常量
			Thumbnails.of(thumbnail.getImage()).size(200, 200)
					.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/test.png")), 0.25f)
					.outputQuality(0.8f).toFile(dest);
		} catch (IOException e) {
			logger.error(e.toString());
			throw new RuntimeException("创建商品缩略:" + e.getMessage());
		}
		//返回相对地址得好处，程序迁移到别的地方也能运行
		return relativeAddr;
	}
	
	
	/**
	 * 处理商品详情图，并返回新生成的图片的相对路径
	 * @param thumbnail
	 * @param targetAddr
	 * @return
	 */
	public static String generateNormalImg(ImageHolder thumbnail, String targetAddr) {
		//获取不重复的随机名
		String realFileName = getRandomFileName();
		//获取文件的扩展名
		String extension = getFileExtension(thumbnail.getImageName());
		//如果目标路径不存在，则自动创建
		makeDirPath(targetAddr);
		//获取文件存储的相对路径(带文件名)
		String relativeAddr = targetAddr + realFileName + extension;
		logger.debug("current relativeAddr is:"+relativeAddr);
		//获取文件要保存到的目标路径
		File dest = new File(PathUtil.getImgBasePath() + relativeAddr);
		logger.debug("current complete addr is:"+ PathUtil.getImgBasePath() + relativeAddr);
		//调用Thumbnails生成带有水印的图片
		try {
			// 该缩略图要用到很多次，所以可以定义为静态常量
			Thumbnails.of(thumbnail.getImage()).size(337, 640)
					.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/test.png")), 0.25f)
					.outputQuality(0.9f).toFile(dest);
		} catch (IOException e) {
			logger.error(e.toString());
			throw new RuntimeException("创建商品详情图失败:" + e.getMessage());
		}
		//返回相对地址得好处，程序迁移到别的地方也能运行
		return relativeAddr;
	}

	/**
	 * 目的：使文件名不重复 生成随机文件名，当前年月日小时分钟秒钟+5位随机数
	 * 
	 * @return
	 */
	public static String getRandomFileName() {
		// 获取随机的五位数
		int rannum = r.nextInt(89999) + 10000;
		String nowTimeStr = sDateFormat.format(new Date());

		return nowTimeStr + rannum;
	}

	/**
	 * 获取输入文件流的拓展名
	 * 
	 * @param thumbnail
	 * @return
	 */
	private static String getFileExtension(String fileName) {

		return fileName.substring(fileName.lastIndexOf("."));
	}

	/**
	 * 创建目标路径所涉及到的目录，即/xxxx///xxx/xxxx.jpg 那么xxx xxx xxx 这三个文件夹都得自动创建
	 * 
	 * @param targetAddr
	 */
	private static void makeDirPath(String targetAddr) {
		String realFileParentPath = PathUtil.getImgBasePath() + targetAddr;
		File dirPath = new File(realFileParentPath);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
	}
	
	/**
	 * storePath是文件的路径还是目录路径,
	 * 如果storePath是文件路径删除该文件,
	 * 如果storePath是目录路径删除该目录下的所有文件
	 * @param storePath
	 */
	public static void deleteFileOrPath(String storePath) {
		File fileOrPath = new File(PathUtil.getImgBasePath() + storePath);
		if(fileOrPath.exists()) {
			if(fileOrPath.isDirectory()) {
				File files[] = fileOrPath.listFiles();
				for(int i = 0; i < files.length; i++) {
					files[i].delete();
				}
			}
			fileOrPath.delete();
		}
	}
}
