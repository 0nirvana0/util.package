package com.common.excel;

import java.util.ArrayList;
import java.util.List;

public class ExcelDataGetter extends ExcelReader {
	private List<Object[]> listCallRecord;

	@Override
	public void getRows(int sheetIndex, int curRow, List<String> rowList) {
		// System.out.println("Sheet:" + sheetIndex + ", Row:" + curRow +
		// ",Data:" + rowList);
		listCallRecord.add(rowList.toArray());

	}

	public List<Object[]> getData(String filename, int sheetId) {
		try {
			listCallRecord = new ArrayList<Object[]>();
			process(filename, sheetId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listCallRecord;
	}

	

}
