package com.k8s.demo.utils.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.k8s.demo.conf.user.SysUser;
import com.k8s.demo.conf.user.UserContext;
import com.k8s.demo.utils.IdMaker;

/**
 * 日志工具类
 * 
 * @author Todd Tan
 * @date 2021年1月11日 上午9:19:15
 */
public class LogUtil {
	private static final Logger LOG = LoggerFactory.getLogger(LogUtil.class);
	public static final String SQL = "sql";
	public static final String REST = "rest";
	public static final String INFO = "info";
	public static final String ONLY_ERROR = "error";
	static final Integer LOG_TYPE_SUCCESS = 0;
	static final Integer LOG_TYPE_FAIL = 1;

	/**
	 * 输出普通日志信息
	 * 
	 * @author Todd Tan
	 * @date 2021年1月11日 上午9:19:05
	 * @param message
	 */
	public static void log(String... msg) {
		if(UserContext.get() != null && ONLY_ERROR.equals(UserContext.get().getLogLevel())) {
			return;
		}
		log(LOG_TYPE_SUCCESS, msg);
	}

	public static void error(String... msg) {
		log(LOG_TYPE_FAIL, msg);
	}

	private static void log(Integer logType, String... msg) {
		if (msg == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < msg.length; i++) {
			sb.append(msg[i] + ",");
		}
		String logMsg = sb.toString().replaceFirst(",$", "");
		if (logType == LOG_TYPE_SUCCESS) {
			log(logMsg);
		} else if (logType == LOG_TYPE_FAIL) {
			error(logMsg);
		}
	}

	public static void log(String message) {
		if(UserContext.get() != null && ONLY_ERROR.equals(UserContext.get().getLogLevel())) {
			return;
		}
		log(message, REST);
	}

	public static void logSql(String message) {
		if(UserContext.get() != null && ONLY_ERROR.equals(UserContext.get().getLogLevel())) {
			return;
		}
		log(message, SQL);
	}

	private static void log(String message, String msgType) {
		JSONObject json = new JSONObject();
		json.put("logType", "___rest___");
		if (UserContext.get() != null) {
			json.put("userName", UserContext.get().getLoginName());
			json.put(Constant.TRACE_ID, UserContext.get().getTraceId());
		}
		json.put("timestamp", System.currentTimeMillis());
		json.put("visitTime", now2String());
		json.put("status", 0);
		json.put("msgType", msgType);
		json.put("env", Constant.ENV);
		json.put("message", message);

		print(json.toJSONString());
	}

	public static void error(String message) {
		JSONObject json = new JSONObject();
		json.put("logType", "___rest___");
		if (UserContext.get() != null) {
			json.put("userName", UserContext.get().getLoginName());
			json.put(Constant.TRACE_ID, UserContext.get().getTraceId());
		}
		json.put("timestamp", System.currentTimeMillis());
		json.put("visitTime", now2String());
		json.put("status", 400);
		json.put("env", Constant.ENV);
		json.put("message", message);

		print(json.toJSONString());
	}

	public static void error(String message, Throwable e) {
		JSONObject json = new JSONObject();
		json.put("logType", "___rest___");
		if (UserContext.get() != null) {
			json.put("userName", UserContext.get().getLoginName());
			json.put(Constant.TRACE_ID, UserContext.get().getTraceId());
		}
		json.put("timestamp", System.currentTimeMillis());
		json.put("visitTime", now2String());
		json.put("status", 400);
		json.put("env", Constant.ENV);
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		json.put("message", message + ": " + stringWriter.toString());

		print(json.toJSONString());
	}

	/**
	 * 输出异常信息
	 * 
	 * @author Todd Tan
	 * @date 2021年1月11日 上午9:20:51
	 * @param e
	 */
	public static void logException(Throwable e) {
		logException(e, REST);
	}

	public static void logSqlException(Throwable e) {
		logException(e, SQL);
	}

	public static void logException(Throwable e, String msgType) {
		JSONObject json = new JSONObject();
		json.put("logType", "___rest___");
		if (UserContext.get() != null) {
			json.put("userName", UserContext.get().getLoginName());
			json.put(Constant.TRACE_ID, UserContext.get().getTraceId());
		}
		json.put("timestamp", System.currentTimeMillis());
		json.put("visitTime", now2String());
		json.put("status", 400);
		json.put("msgType", msgType);
		json.put("env", Constant.ENV);

		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		json.put("message", stringWriter.toString());

		print(json.toJSONString());
	}

	/**
	 * 输出异常信息
	 * 
	 * @author Todd Tan
	 * @date 2021年1月11日 上午9:22:03
	 * @param message
	 * @param e
	 */
	public static void logException(String message, Throwable e) {
		logException(message, e, REST);
	}

	public static void logSqlException(String message, Throwable e) {
		logException(message, e, SQL);
	}

	public static void logException(String message, Throwable e, String msgType) {
		JSONObject json = new JSONObject();
		json.put("logType", "___rest___");
		if (UserContext.get() != null) {
			json.put("userName", UserContext.get().getLoginName());
			json.put(Constant.TRACE_ID, UserContext.get().getTraceId());
		}
		json.put("timestamp", System.currentTimeMillis());
		json.put("visitTime", now2String());
		json.put("status", 400);
		json.put("msgType", msgType);
		json.put("env", Constant.ENV);

		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		json.put("message", message + ": " + stringWriter.toString());

		print(json.toJSONString());
	}

	private static void print(String log) {
		LOG.info("\r\n" + log);
	}
	
	/**
	 * !!!只能后台任务使用, 接口线程使用会修改当前登录用户导致出错!!!
	 * @author Todd Tan
	 * @date 2021年2月8日 下午2:56:03
	 */
	public static void initTraceId() {
		init(null, INFO);
	}
	
	public static void initTraceId(String name) {
		init(null, INFO);
	}
	
	public static void initTraceId(String name, String logLevel) {
		init(null, logLevel);
	}
	
	private static void init(String name, String logLevel) {
		/**
		 * 仅后台任务可用, 接口方法禁止调用
		 */
		if(UserContext.get() != null) {
			return;
		}
		SysUser user = new SysUser();
		user.setLoginName(name);
		user.setTraceId(String.valueOf(IdMaker.get()));
		user.setLogLevel(logLevel);
		UserContext.set(user);
		log(name);
	}

	/**
	 * 获取当前时间
	 * 
	 * @author Todd Tan
	 * @date 2021年1月11日 上午9:23:38
	 * @return
	 */
	private static String now2String() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		return sdf.format(new Date());
	}
}
