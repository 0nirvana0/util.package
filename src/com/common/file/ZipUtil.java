package com.common.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class ZipUtil {

	/**
	 * zip压缩文件
	 * 
	 * @param dir
	 * @param zippath
	 */
	// public static void compressFiles2Zip(String dir, String zippath) {
	// List<String> paths = getFiles(dir);
	// compressFiles2Zip(paths.toArray(new String[paths.size()]), zippath,
	// dir);
	// }

	public static void compressFiles2Zip(File[] files, String zipFilePath) {
		compressFiles2Zip(files, new File(zipFilePath));
	}

	/**
	 * 把文件压缩成zip格式
	 * 
	 * @param files
	 *            需要压缩的文件
	 * @param zipFilePath
	 *            压缩后的zip文件路径 ,如"D:/test/aa.zip";
	 */
	public static void compressFiles2Zip(File[] files, File zipFile) {
		if (files == null || files.length <= 0) {
			return;
		}
		ZipArchiveOutputStream zaos = null;
		try {
			zaos = new ZipArchiveOutputStream(zipFile);
			// Use Zip64 extensions for all entries where they are required
			zaos.setUseZip64(Zip64Mode.AsNeeded);
			// 将每个文件用ZipArchiveEntry封装
			// 再用ZipArchiveOutputStream写到压缩文件中
			for (File file : files) {
				String out = file.getPath().replace("data\\zip\\compress\\", "");
				System.out.println(out);
				ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, out);
				zaos.putArchiveEntry(zipArchiveEntry);
				// if (file.isDirectory()) {
				// continue;
				// }
				InputStream is = null;
				try {
					is = new BufferedInputStream(new FileInputStream(file));
					byte[] buffer = new byte[1024 * 5];
					int len = -1;
					while ((len = is.read(buffer)) != -1) {
						// 把缓冲区的字节写入到ZipArchiveEntry
						zaos.write(buffer, 0, len);
					}
					zaos.closeArchiveEntry();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (is != null)
						is.close();
				}

			}

			zaos.finish();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (zaos != null) {
					zaos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 把zip文件解压到指定的文件夹
	 * 
	 * @param zipFilePath
	 *            zip文件路径, 如 "D:/test/aa.zip"
	 * @param saveFileDir
	 *            解压后的文件存放路径, 如"D:/test/" ()
	 */
	public static void unzip(File zipFilePath, File saveFileDir) {

		InputStream is = null;
		// can read Zip archives
		ZipArchiveInputStream zais = null;
		try {
			is = new FileInputStream(zipFilePath);
			zais = new ZipArchiveInputStream(is);
			ArchiveEntry archiveEntry = null;
			// 把zip包中的每个文件读取出来
			// 然后把文件写到指定的文件夹
			while ((archiveEntry = zais.getNextEntry()) != null) {
				// 获取文件名
				String entryFileName = archiveEntry.getName();
				// 构造解压出来的文件存放路径
				String entryFilePath = saveFileDir + File.separator + entryFileName;
				byte[] content = new byte[(int) archiveEntry.getSize()];
				OutputStream os = null;
				try {
					// 把解压出来的文件写到指定路径
					File entryFile = new File(entryFilePath);
					if (archiveEntry.isDirectory()) { // 文件夹
						if (!entryFile.exists()) {
							entryFile.mkdirs();
						}
					} else {
						os = new BufferedOutputStream(new FileOutputStream(entryFile));
						int len = -1;
						while ((len = zais.read(content)) != -1) {
							os.write(content, 0, len);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (os != null) {
						os.flush();
						os.close();
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (zais != null) {
					zais.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 解压所有ZIP文件到指定文件夹下（包含所有压缩包里的ZIP）
	 * 
	 * @param srcZipPath
	 * @param dstDirectoryPath
	 */
	public static void unZipAll(String srcZipPath, String dstDirectoryPath) {
		File zip = new File(srcZipPath);
		if (zip.isFile() && !srcZipPath.toLowerCase().endsWith(".zip")) {
			return;
		}

		if (dstDirectoryPath.toLowerCase().endsWith(".zip")) {
			dstDirectoryPath = dstDirectoryPath.substring(0, dstDirectoryPath.length() - 4);
		}

		File destinationFolder = new File(dstDirectoryPath);
		if (!destinationFolder.exists()) {
			destinationFolder.mkdirs();
		}
		if (srcZipPath.toLowerCase().endsWith(".zip")) {
			unzip(zip, destinationFolder);
		}

		File[] files = destinationFolder.listFiles();
		String fileName = "";
		for (int i = 0; i < files.length; i++) {
			fileName = files[i].getPath();
			unZipAll(fileName, fileName);
		}
	}
}
