package com.common.excel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelDataGetter extends ExcelReader {
	private List<Object[]> listAllRecord;

	@Override
	public void getRows(int sheetIndex, int curRow, List<String> rowList) {
		// System.out.println("Sheet:" + sheetIndex + ", Row:" + curRow +
		// ",Data:" + rowList);
		listAllRecord.add(rowList.toArray());

	}

	public List<Object[]> getData(String filename, int sheetId) {
		try {
			listAllRecord = new ArrayList<Object[]>();
			process(filename, sheetId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listAllRecord;
	}

	/**
	 * 取excel第一行的最大列 获取长度统一的数组列表
	 * 
	 * @param filename
	 * @param sheetId
	 * @return
	 */
	public List<Object[]> getEexcelData(String filename, int sheetId) {
		getData(filename, sheetId);

		List<Object[]> allRecord = new ArrayList<Object[]>();
		int maxCol = listAllRecord.get(0).length;
		int length = 0;
		for (Object[] objects : listAllRecord) {
			length = objects.length;
			if (length == 0)
				continue;
			if (length <= maxCol) {
				// 数组扩容
				objects = Arrays.copyOf(objects, maxCol);
				for (int i = length; i < maxCol; i++) {
					objects[i] = "";
				}
			}
			allRecord.add(objects);
		}

		return allRecord;
	}

}
