package com.common.file;

import java.io.File;

import com.common.time.FormatDuring;

public class Test {

	public static void main(String[] args) {
		long lStart = System.currentTimeMillis();
		testText();
		System.out.println(FormatDuring.formatDuring(lStart, System.currentTimeMillis()));
	};

	public static void testText() {
		try {
			String file = "data/text/test.txt";

			TextUtil.writeTxtFile("\ntext", new File(file), true);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
