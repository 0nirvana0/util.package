package com.common.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

public class Download {
	public static void main(String[] args) {
		String mapPath = "C:/Users/liuqiang/Desktop/电子地图/Content/Map/";
		String titlePath = "C:/Users/liuqiang/Desktop/电子地图/Content/Title/";
		String rootUrl = "http://www.16p.top/Title/";
		String titleUrl = "";
		File[] mapFiles = new File(mapPath).listFiles();
		for (File file : mapFiles) {
			titleUrl = file.getPath();
			titleUrl = titleUrl.substring(titleUrl.lastIndexOf(File.separator) + 1);
			downloadPicture(rootUrl + titleUrl, titlePath + titleUrl);
		}

	}

	// 链接url下载图片
	private static void downloadPicture(String urlList, String outPath) {
		URL url = null;
		try {
			url = new URL(urlList);
			DataInputStream dataInputStream = new DataInputStream(url.openStream());

			FileOutputStream fileOutputStream = new FileOutputStream(new File(outPath));
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			int length;

			while ((length = dataInputStream.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			fileOutputStream.write(output.toByteArray());
			dataInputStream.close();
			fileOutputStream.close();
		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}
}
