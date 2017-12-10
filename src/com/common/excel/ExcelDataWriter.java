package com.common.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * XSSFWorkbook 基本数据超过20W或者2M时会溢出
 * 
 * Workbook wb = new SXSSFWorkbook(5000);
 * 在生成Workbook时给工作簿一个内存数据存在条数，这样一旦这个Workbook
 * 中数据量超过5000就会写入到磁盘中，减少内存的使用量来提高速度和避免溢出。
 * 
 * @author liuqiang
 *
 */
public class ExcelDataWriter {

	public void writeOut(List<Object[]> data, String path) {
		int excelRow = 0;
		FileOutputStream os = null;
		Workbook wb = null;

		wb = new SXSSFWorkbook(5000);
		// 获得该工作区的第一个sheet
		Sheet sheet1 = wb.createSheet();

		// 循环读取并写入
		if (data != null && data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				// 明细行
				Row contentRow = sheet1.createRow(excelRow++);
				Object[] reParam = data.get(i);

				for (int j = 0; j < reParam.length; j++) {
					Cell cell = contentRow.createCell(j);
					cell.setCellType(CellType.STRING);
					cell.setCellValue(reParam[j].toString());

				}
			}
			// 自动列宽
			((SXSSFSheet) sheet1).trackAllColumnsForAutoSizing();
			for (int i = 0; i < data.get(0).length; i++) {				
				sheet1.autoSizeColumn(i);
			}
		}

		try {
			os = new FileOutputStream(path);
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
					wb.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
