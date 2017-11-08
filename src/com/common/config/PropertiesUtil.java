package com.common.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {
	private String path;

	/**
	 * 构造方法，初始化PropertiesUtil对象
	 */
	private PropertiesUtil(String path) {
		this.path = path;

	}

	/**
	 * 获取构造器，根据类初始化PropertiesUtil对象
	 * 
	 * @param Class
	 *            Class对象
	 * @return Logger对象
	 */
	public static PropertiesUtil getPropertiesUtil(String url) {
		return new PropertiesUtil(url);
	}

	public Properties load() {
		Properties prop = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(path));
			prop.load(new InputStreamReader(in, "UTF-8"));
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}

	public String getProperty(String key) {
		Properties prop = null;
		InputStream in = null;
		try {
			prop = new Properties();
			// 读取属性文.properties
			in = new BufferedInputStream(new FileInputStream(path));
			prop.load(new InputStreamReader(in, "UTF-8"));
			// 加载属性列表
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop.getProperty(key);
	}

	public void editProperty(String key, String value) {
		Properties prop = load();
		FileOutputStream oFile = null;
		try {
			// 保存属性到.properties文件
			oFile = new FileOutputStream(path);// true表示追加打开
			prop.setProperty(key, value);
			prop.store(oFile, "Copyright (c) liu qiang");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oFile != null) {
				try {
					oFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 相当新建一個属性
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value) {
		Properties prop = null;
		FileOutputStream oFile = null;
		try {
			prop = new Properties();
			// 保存属性到.properties文件
			oFile = new FileOutputStream(path, false);// true表示追加打开
			prop.setProperty(key, value);
			prop.store(oFile, "The New properties file");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oFile != null) {
				try {
					oFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
