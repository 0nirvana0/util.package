package com.common.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

public class Logger2 {

	private org.apache.logging.log4j.Logger logger;
	static {
		try {
			File conFile = new File("config/statistics/log4j2.xml");
			ConfigurationSource source = new ConfigurationSource(new FileInputStream(conFile));
			Configurator.initialize(null, source);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 构造方法，初始化Log4j的日志对象
	 */
	private Logger2(org.apache.logging.log4j.Logger log4jLogger) {
		logger = log4jLogger;
	}

	/**
	 * 获取构造器，根据类初始化Logger对象
	 * 
	 * @param Class
	 *            Class对象
	 * @return Logger对象
	 */
	public static Logger2 getLogger(Class<?> classObject) {
		return new Logger2(LogManager.getLogger(classObject));
	}

	/**
	 * 获取构造器，根据类名初始化Logger对象
	 * 
	 * @param String
	 *            类名字符串
	 * @return Logger对象
	 */
	public static Logger2 getLogger(String loggerName) {
		return new Logger2(LogManager.getLogger(loggerName));
	}

	public void debug(Object object) {
		logger.debug(object);
	}

	public void debug(Object object, Throwable e) {
		logger.debug(object, e);
	}

	public void info(Object object) {
		logger.info(object);
	}

	public void info(Object object, Throwable e) {
		logger.info(object, e);
	}

	public void warn(Object object) {
		logger.warn(object);
	}

	public void warn(Object object, Throwable e) {
		logger.warn(object, e);
	}

	public void error(Object object) {
		logger.error(object);
	}

	public void error(Object object, Throwable e) {
		logger.error(object, e);
	}

	public void fatal(Object object) {
		logger.fatal(object);
	}

	public String getName() {
		return logger.getName();
	}

	public org.apache.logging.log4j.Logger getLog4j2Logger() {
		return logger;
	}

	public boolean equals(Logger2 newLogger) {
		return logger.equals(newLogger.getLog4j2Logger());
	}
}