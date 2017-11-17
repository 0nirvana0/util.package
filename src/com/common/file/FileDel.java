package com.common.file;

import java.io.File;

/**
 * 删除文件夹下所有文件
 * 
 * @author liuqiang
 *
 */
public class FileDel {
	public static void main(String[] args) {
		File path = new File("E:/new");
		deleteAllFilesOfDir(path);

	}

	public static void deleteAllFilesOfDir(File path) {
		if (!path.exists())
			return;
		if (path.isFile()) {
			path.delete();
			return;
		}
		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			deleteAllFilesOfDir(files[i]);
		}
		path.delete();
	}
}
