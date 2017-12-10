package com.common.file;

import java.io.File;
import java.io.FileOutputStream;

import com.github.junrar.Archive;
import com.github.junrar.extract.ExtractArchive;
import com.github.junrar.rarfile.FileHeader;

/**
 * 压缩解压工具类
 * 
 * @author liuqiang
 *
 */
public class RarUtil {
	/**
	 * 根据原始rar路径，解压到指定文件夹下.
	 * 
	 * @param srcRarPath
	 *            原始rar路径
	 * @param dstDirectoryPath
	 *            解压到的文件夹
	 */
	public static void unRarFile(String srcRarPath, String dstDirectoryPath) {
		if (!srcRarPath.toLowerCase().endsWith(".rar")) {
			System.out.println("非rar文件！");
			return;
		}
		File dstDiretory = new File(dstDirectoryPath);
		if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
			dstDiretory.mkdirs();
		}
		Archive a = null;
		try {
			a = new Archive(new File(srcRarPath));
			if (a != null) {
				// a.getMainHeader().print(); // 打印文件信息.
				FileHeader fh = a.nextFileHeader();
				while (fh != null) {
					// 防止文件名中文乱码问题的处理
					String fileName = fh.getFileNameW().isEmpty() ? fh.getFileNameString() : fh.getFileNameW();
					if (fh.isDirectory()) { // 文件夹
						File fol = new File(dstDirectoryPath + File.separator + fileName);
						fol.mkdirs();
					} else { // 文件
						File out = new File(dstDirectoryPath + File.separator + fileName.trim());
						try {
							if (!out.exists()) {
								if (!out.getParentFile().exists()) {
									// 相对路径可能多级，可能需要创建父目录.
									out.getParentFile().mkdirs();
								}

							}

							FileOutputStream os = new FileOutputStream(out);
							a.extractFile(fh, os);
							os.close();

						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					fh = a.nextFileHeader();
				}
				a.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解压所有RAR文件到指定文件夹下（包含所有压缩包里的RAR）
	 * 
	 * @param srcRarPath
	 * @param dstDirectoryPath
	 */
	public static void unRarAllFile(String srcRarPath, String dstDirectoryPath) {
		ExtractArchive extractArchive = new ExtractArchive();

		File rar = new File(srcRarPath);
		if (rar.isFile() && !srcRarPath.toLowerCase().endsWith(".rar")) {
			return;
		}

		if (dstDirectoryPath.toLowerCase().endsWith(".rar")) {
			dstDirectoryPath = dstDirectoryPath.substring(0, dstDirectoryPath.length() - 4);
		}
		File destinationFolder = new File(dstDirectoryPath);
		if (!destinationFolder.exists()) {// 目标目录不存在时，创建该文件夹
			destinationFolder.mkdirs();
		}
		if (srcRarPath.toLowerCase().endsWith(".rar")) {
			extractArchive.extractArchive(rar, destinationFolder);
		}

		File[] files = destinationFolder.listFiles();
		String fileName = "";

		for (int i = 0; i < files.length; i++) {
			fileName = files[i].getPath();
			unRarAllFile(fileName, fileName);
		}

	}

}
