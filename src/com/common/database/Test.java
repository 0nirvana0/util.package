package com.common.database;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.common.time.FormatDuring;

public class Test {

	public static void main(String[] args) {
		long lStart = System.currentTimeMillis();
		//getTables();
		backup();
		System.out.println(FormatDuring.formatDuring(lStart, System.currentTimeMillis()));
	};

	/**
	 * 测试获取本数据库的所有表名
	 */
	public static List<String> getTables() {
		List<String> tables = null;
		try {
			tables = DBUtil.getDBUtil().getTables();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(tables);
		return tables;
	}

	/**
	 * 备份
	 */
	public static void backup() {
		String tableName = "shopping_goodsbrand";
		try {
			String data = DBUtil.getDBUtil().backup(tableName).toString();
			System.out.println(data);
		} catch (InterruptedException e) {

			e.printStackTrace();
		} catch (ExecutionException e) {

			e.printStackTrace();
		}

	}
}
