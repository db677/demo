package com.k8s.demo.utils;

import com.k8s.demo.conf.exception.CustomException;

/**
 * 断言
 * 
 * @author Todd Tan
 * @date 2019年7月29日 下午3:02:54
 */
public class Assert {

	private Assert() {

	}

	/**
	 * 断言表达式结果为是，否则抛出异常
	 * 
	 * @param expression boolean表达式
	 * @param msg        异常消息
	 * @throws CustomException
	 */
	public static void isTrue(boolean expression, String msg) {
		if (!expression) {
			throw new CustomException(msg);
		}
	}

	/**
	 * 断言对象不空，否则抛出异常
	 * 
	 * @param object 目标对象
	 * @param msg    异常消息
	 * @throws CustomException
	 */
	public static void notNull(Object object, String msg) {
		if (object == null) {
			throw new CustomException(msg);
		}
	}

	/**
	 * 断言对象为空，否则抛出异常
	 * 
	 * @param object 目标对象
	 * @param msg    异常消息
	 * @throws CustomException
	 */
	public static void isNull(Object object, String msg) {
		if (object != null) {
			throw new CustomException(msg);
		}
	}

	/**
	 * 断言字符串对象和内容不为空，否则抛出异常
	 * 
	 * @param str 字符串对象
	 * @param msg 异常消息
	 * @throws CustomException
	 */
	public static void notEmpty(String str, String msg) {
		if (str == null || "".equals(str.trim())) {
			throw new CustomException(msg);
		}
	}
}
