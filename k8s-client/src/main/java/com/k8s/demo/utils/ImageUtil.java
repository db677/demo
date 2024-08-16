package com.k8s.demo.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class ImageUtil {
	static final String PNG_FMT = "png";
	/**
	 * size
	 */
	static final int WIDTH = 1000;
	static final int HEIGHT = 1000;
	/**
	 * new path
	 */
	static final String NEW_FOLDER_NAME = "generator_new_file";
	static final String NEW_PATH = "/" + NEW_FOLDER_NAME;
	static final String PNG_REGEX = "\\." + PNG_FMT;
	static final Pattern p = Pattern.compile(PNG_REGEX);
	static final Pattern f = Pattern.compile(NEW_FOLDER_NAME);
	
	public static void main(String[] args) {
		generator("D:/BaiduNetdiskDownload/20211206/91-游戏美术资料素材 光效素材 68套技能特效 动画序列帧图PNG 760P/游戏美术资料素材 光效素材 68套技能特效 动画序列帧图PNG 760P/三原色 技能光效合集");
	}
	
	private static void generator(String path) {
		File file = new File(path);
		File[] fileArr = file.listFiles();
		// 先排序
		List<File> fileList = Arrays.asList(fileArr);
		Collections.sort(fileList, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if (o1.isDirectory() && o2.isFile()) {
					return -1;
				}
				if (o1.isFile() && o2.isDirectory()) {
					return 1;
				}
				return sortByNum(o1.getName(), o2.getName());
			}
		});
		int num = 100;
		for(File item: fileList) {
			if(item.isDirectory()) {
				if(f.matcher(item.getName()).find()) {
					continue;
				}
				generator(item.getPath());
			} else {
				if(p.matcher(item.getName()).find()) {
					generator(item, file.getPath(), num++);
				}
			}
		}
	}
	
	private static int sortByNum(String name1, String name2) {
		try {
			String n1 = name1.replaceAll("\\D+", "");
			String n2 = name2.replaceAll("\\D+", "");
			int x = Integer.parseInt(n1);
			int y = Integer.parseInt(n2);
			return x-y;
		} catch(Exception e) {
			System.out.println(name1 + "==================" + name2);
			// e.printStackTrace();
			return name1.compareTo(name2);
		}
	}
	
	private static void generator(File file, String folder, int num) {
		File path = new File(folder + NEW_PATH);
		if(!path.exists()) {
			path.mkdirs();
		}
		generatorFile(file, path.getPath(), num);
	}
	
	private static void generatorFile(File file, String folder, int num) {
		BufferedImage image = null;
		BufferedImage newImg = null;
		try {
			image = ImageIO.read(file);
			//
			ColorModel cm = ColorModel.getRGBdefault();
			WritableRaster wr = cm.createCompatibleWritableRaster(WIDTH, HEIGHT);
			newImg = new BufferedImage(cm, wr, cm.isAlphaPremultiplied(), null);
			// 计算位置
			int left = (WIDTH - image.getWidth()) / 2;
			int top = (HEIGHT - image.getHeight()) / 2;
			//
			Graphics2D g2d = newImg.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.drawImage(image, left, top, null);
			File outfile = new File(folder + "/" + num + "." + PNG_FMT);
			ImageIO.write(newImg, PNG_FMT, outfile);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(image != null) {
				image.getGraphics().dispose();
			}
			if(newImg != null) {
				newImg.getGraphics().dispose();
			}
		}
	}
}

/**
public static void test(File file) throws IOException {
	BufferedImage image = ImageIO.read(file);
	//
	BufferedImage newImg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	Graphics2D g2d = newImg.createGraphics();
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.drawImage(image, 0, 0, null);
	File outfile = new File("d:/test1.png");
	ImageIO.write(newImg, "png", outfile);
}
*/