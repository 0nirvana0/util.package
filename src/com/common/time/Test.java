package com.common.time;

public class Test {
	public static void main(String[] args) {
		long lStart = System.currentTimeMillis();
		testFormatDuring();
		System.out.println(FormatDuring.formatDuring(lStart, System.currentTimeMillis()));
	};

	public static void testFormatDuring() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
