package com.common.excel;

import java.util.Arrays;
import java.util.List;

import com.common.time.FormatDuring;

public class Test {

	public static void main(String[] args) {
		long lStart = System.currentTimeMillis();
		testExcelDataWriter();
		System.out.println(FormatDuring.formatDuring(lStart, System.currentTimeMillis()));
	};

	public static void testExcelReader() {
		try {
			String file = "data/excel/test1.xlsx";

			ExcelReader reader = new ExcelReader() {
				public void getRows(int sheetIndex, int curRow, List<String> rowList) {
					System.out.println("Sheet:" + sheetIndex + ", Row:" + curRow + ", Data:" + rowList);
				}
			};
			reader.process(file, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void testExcelDataGetter() {
		try {
			String file = "data/excel/test1.xlsx";

			ExcelDataGetter getter = new ExcelDataGetter();
			List<Object[]> data = getter.getData(file, 1);
			for (Object[] objects : data) {
				System.out.println(Arrays.toString(objects));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void testExcelDataWriter() {
		try {
			String out = "data/excel/out.xlsx";
			String file = "data/excel/test1.xlsx";

			ExcelDataGetter getter = new ExcelDataGetter();
			List<Object[]> data = getter.getData(file, 1);

			ExcelDataWriter writer = new ExcelDataWriter();
			writer.writeOut(data, out);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
