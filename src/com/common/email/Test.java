package com.common.email;

import java.util.Arrays;
import java.util.List;

import com.common.config.PropertiesUtil;
import com.common.time.FormatDuring;

public class Test {

	public static void main(String[] args) {
		long lStart = System.currentTimeMillis();
		testEmail();
		System.out.println(FormatDuring.formatDuring(lStart, System.currentTimeMillis()));
	};

	public static void testEmail() {

		PropertiesUtil ep = PropertiesUtil.getPropertiesUtil("config/email/email.properties");
		EmailUtil emailUtil = EmailUtil.getEmail(ep.getProperty("hostName"), ep.getProperty("account"),
				ep.getProperty("password"));

		emailUtil.sendSimpleMail("172446628@qq.com", "主题", "内容");

	}

}
