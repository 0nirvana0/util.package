package com.common.math;

import java.math.BigInteger;

import com.common.time.FormatDuring;

public class Test {

	public static void main(String[] args) {
		long lStart = System.currentTimeMillis();
		testRightsHelper();
		System.out.println(FormatDuring.formatDuring(lStart, System.currentTimeMillis()));
	};

	public static void testRightsHelper() {
		BigInteger num = BigInteger.valueOf(0);
		// for (int i = 0; i < 200; i++) {
		// num = num.setBit(i);
		// }
		num = num.setBit(1);

		System.out.println(num);
		System.out.println(num.testBit(0));
		System.out.println(num.testBit(1));
		System.out.println(num.testBit(2));
	}

}
