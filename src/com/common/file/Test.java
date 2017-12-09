package com.common.file;

import java.io.File;

import com.common.time.FormatDuring;

public class Test {

	public static void main(String[] args) {
		long lStart = System.currentTimeMillis();
		rar();
		System.out.println(FormatDuring.formatDuring(lStart, System.currentTimeMillis()));
	};

	public static void testText() {
		try {
			String file = "data/text/test.txt";

			TextUtil.writeTxtFile("\ntext", new File(file), true);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static void rar() {
		try {
			String srcRarPath = "data/rar/come.rar";
			String dstDirectoryPath = "data/rar/come";
			FileUtil.deleteAllFilesOfDir(dstDirectoryPath);
			RarUtil.unRarFile(srcRarPath, dstDirectoryPath);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void File() {
		File folder = new File("e:/temp/20160317");// 默认目录
		String keyword = "grib1";
		if (!folder.exists()) {// 如果文件夹不存在
			System.out.println("目录不存在：" + folder.getAbsolutePath());
			return;
		}
		// --------------------------------------------------------------------------
		File[] result = FileUtil.searchFile(folder, keyword);// 调用方法获得文件数组
		// File[] result = searchFile(folder);
		// --------------------------------------------------------------------------
		System.out.println("在 " + folder + " 以及所有子文件时查找对象" + keyword);
		System.out.println("查找了" + FileUtil.countFiles + " 个文件，" + FileUtil.countFolders + " 个文件夹，共找到 " + result.length
				+ " 个符合条件的文件：");

		for (int i = 0; i < result.length; i++) {// 循环显示文件
			File file = result[i];
			System.out.println(file.getAbsolutePath() + " ");// 显示文件绝对路径
		}

	}
}
