package com.common.excel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Excel超大数据读取，抽象Excel2007读取器。 excel2007的底层数据结构是xml文件，采用SAX的事件驱动的方法解析xml，
 * 需要继承DefaultHandler，在遇到文件内容时， 事件会触发，这种做法可以大大降低 内存的耗费，特别使用于大数据量的文件。
 * 
 * @author liuqiang
 *
 */
public abstract class ExcelReader extends DefaultHandler {
	// 共享字符串表
	private SharedStringsTable sst;

	private int sheetIndex = -1;

	private List<String> rowList = new ArrayList<String>();

	// 当前行
	private int curRow = 0;
	// 当前列
	private int curCol = 0;

	private String cellS;

	private String cellType;

	private boolean valueFlag;

	private StringBuilder value;

	private String result;

	private int idx;

	// 定义当前元素的位置，如A6和A8等
	private String ref = null;

	// 定义当前元素的所在列
	private int newCellCol;

	// 定义当前元素的所在列和上列元素列差
	private int colNum;

	/**
	 * 遍历工作簿中所有的电子表格
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public void process(String filename) throws Exception {
		OPCPackage pkg = OPCPackage.open(filename);
		XSSFReader r = new XSSFReader(pkg);
		SharedStringsTable sst = r.getSharedStringsTable();
		XMLReader parser = fetchSheetParser(sst);
		Iterator<InputStream> sheets = r.getSheetsData();
		while (sheets.hasNext()) {
			curRow = 0;
			sheetIndex++;
			InputStream sheet = sheets.next();
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
			sheet.close();
		}
	}

	/**
	 * 只遍历一个电子表格，其中sheetId为要遍历的sheet索引，从1开始，1-3
	 * 
	 * @param filename
	 * @param sheetId
	 * @throws Exception
	 */
	public void process(String filename, int sheetId) throws Exception {
		OPCPackage pkg = OPCPackage.open(filename);
		XSSFReader reader = new XSSFReader(pkg);
		SharedStringsTable sst = reader.getSharedStringsTable();
		XMLReader parser = fetchSheetParser(sst);
		// 根据 rId# 或 rSheet# 查找sheet
		InputStream sheet2 = reader.getSheet("rId" + sheetId);
		InputSource sheetSource = new InputSource(sheet2);
		parser.parse(sheetSource);
		sheet2.close();
	}

	public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
		XMLReader parser = XMLReaderFactory.createXMLReader();
		this.sst = sst;
		parser.setContentHandler(this);
		return parser;
	}

	private String convertCellValue() {
		result = value.toString();
		if ("s".equals(cellType)) { // 字符串
			idx = Integer.parseInt(result);
			result = new XSSFRichTextString(sst.getEntryAt(idx)).toString().trim();
		} else if ("n".equals(cellType)) {
			if ("2".equals(cellS)) { // 日期
				result = HSSFDateUtil.getJavaDate(Double.valueOf(result)).toString();
				// System.out.println("cellS2:" + result);
			}
			if ("1".equals(cellS)) { // 数字
				// System.out.println("cellS1:" + result);
			}
		} else if ("b".equals(cellType)) {
			result = "0".equals(result) ? "FALSE" : "TRUE";
		}
		return result;
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		// System.out.println("startElement: " + localName + ", " + name + ", "
		// + attributes);
		if ("sheetData".equals(name)) {
			sheetIndex++;
		} else if ("row".equals(name)) {
			curRow++;
			curCol = 0;
		} else if ("c".equals(name)) {// 注意bug 单元格无值，值不遍历 v 或者直接跳过c v

			// 当前单元格的位置
			ref = attributes.getValue("r");

			cellS = attributes.getValue("s");
			cellType = attributes.getValue("t");

		} else if ("v".equals(name)) {
			valueFlag = true;
			value = new StringBuilder();
		}
	}

	// 若一个节点，比如<name>michael</name>,在执行完characters后会执行该方法。
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {

		if ("row".equals(name)) {
			getRows(sheetIndex, curRow, rowList);
			rowList.clear();
		} else if ("c".equals(name)) {// 注意bug 单元格无值，不遍历 v

		} else if ("v".equals(name)) {
			// 补全单元格之间的空单元格
			newCellCol = countNullCell(ref, "@");
			// TODO 和上面的逻辑应该可以合并
			if (curCol != newCellCol) {
				// 1
				// <x:c r="A1" t="s">
				// <x:v>0</x:v>
				// </x:c>
				// <x:c r="C1" t="s">
				// <x:v>1</x:v>
				// </x:c>
				// 2
				// <x:c r="D2" s="1" t="s">
				// <x:v>4</x:v>
				// </x:c>
				// <x:c r="E2" s="1" />
				// <x:c r="F2" s="1" />
				// <x:c r="G2" s="1" t="s">
				// <x:v>5</x:v>
				// </x:c>
				colNum = newCellCol - curCol;
				for (int i = 0; i < colNum; i++) {
					rowList.add(curCol, "");
					curCol++;
				}

			}

			rowList.add(curCol, convertCellValue());
			valueFlag = false;
			curCol++;

		}
		// System.out.println("endElement: " + localName + ", " + name);
	}

	/**
	 * 计算两个单元格之间的单元格数目(同一行)
	 * 
	 * @param ref
	 * @param preRef
	 * @return
	 */
	public int countNullCell(String ref, String preRef) {
		// excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD
		String xfd = ref.replaceAll("\\d+", "");
		String xfd_1 = preRef.replaceAll("\\d+", "");

		xfd = fillChar(xfd, 3, '@', true);
		xfd_1 = fillChar(xfd_1, 3, '@', true);

		char[] letter = xfd.toCharArray();
		char[] letter_1 = xfd_1.toCharArray();
		int res = (letter[0] - letter_1[0]) * 26 * 26 + (letter[1] - letter_1[1]) * 26 + (letter[2] - letter_1[2]);
		return res - 1;
	}

	/**
	 * 字符串的填充
	 * 
	 * @param str
	 * @param len
	 * @param let
	 * @param isPre
	 * @return
	 */
	String fillChar(String str, int len, char let, boolean isPre) {
		int len_1 = str.length();
		if (len_1 < len) {
			if (isPre) {
				for (int i = 0; i < (len - len_1); i++) {
					str = let + str;
				}
			} else {
				for (int i = 0; i < (len - len_1); i++) {
					str = str + let;
				}
			}
		}
		return str;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// 得到单元格内容的值
		if (valueFlag)
			value.append(ch, start, length);
	}

	/**
	 * 该方法自动被调用，每读一行调用一次，在方法中写自己的业务逻辑即可
	 * 
	 * @param sheetIndex
	 *            工作簿序号
	 * @param curRow
	 *            处理到第几行
	 * @param rowList
	 *            当前数据行的数据集合
	 */
	protected abstract void getRows(int sheetIndex, int curRow, List<String> rowList);

}
