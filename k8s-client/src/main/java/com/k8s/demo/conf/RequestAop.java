package com.k8s.demo.conf;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.k8s.demo.conf.exception.CustomException;
import com.k8s.demo.conf.user.SysUser;
import com.k8s.demo.conf.user.UserContext;
import com.k8s.demo.utils.IdMaker;
import com.k8s.demo.utils.log.Constant;
import com.k8s.demo.utils.log.LogUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Todd Tan
 * @date 2020年11月1日 下午1:08:52
 */
@Component
@Aspect
@Order(3999)
public class RequestAop {
	static final Logger LOG = LoggerFactory.getLogger(RequestAop.class);

	@Pointcut("execution(* com.k8s..*.*Controller.*(..))")
	public void init() {

	}

	@Before("init()")
	public void beforeAdvice(JoinPoint joinPoint) {

	}

	/**
	 * 获取url参数
	 * 
	 * @author Todd Tan
	 * @date 2020年11月10日 下午5:30:29
	 * @param request
	 * @param urlArgs
	 * @return
	 */
	private JSONObject urlArgs2String(HttpServletRequest request, Enumeration<String> urlArgs) {
		JSONObject json = new JSONObject();
		if (urlArgs == null) {
			return json;
		}
		try {
			while (urlArgs.hasMoreElements()) {
				String key = urlArgs.nextElement();
				json.put(key, request.getParameter(key));
			}
		} catch (Exception e) {
			LOG.error("记录url中的参数出错, " + e.getMessage());
		}

		return json;
	}

	/**
	 * 获取body参数
	 * 
	 * @author Todd Tan
	 * @date 2020年11月10日 下午5:30:36
	 * @param request
	 * @param bodyArgs
	 * @return
	 */
	private String bodyArgs2String(HttpServletRequest request, Object[] bodyArgs) {
		if ("GET".equals(request.getMethod())) {
			return "";
		}
		String body = "";
		try {
			body = JSONArray.toJSONString(bodyArgs);
		} catch (Exception e) {
			LOG.error("记录body中的参数出错, " + e.getMessage());
		}

		return body;
	}

	/**
	 * 获取接口名称
	 * 
	 * @author Todd Tan
	 * @date 2020年11月10日 下午5:30:42
	 * @param pjp
	 * @return
	 */
	private String getApiName(ProceedingJoinPoint pjp) {
		try {
			/**
			 * 获取类上面的注解
			 */
			String apiTags = "";
			if (pjp.getTarget().getClass().isAnnotationPresent(Api.class)) {
				Api api = pjp.getTarget().getClass().getAnnotation(Api.class);
				String[] tags = api.tags();
				if (tags != null) {
					apiTags = tags[0];
				}
			}
			/**
			 * 获取方法上面的注解
			 */
			String value = "";
			Signature signature = pjp.getSignature();
			MethodSignature methodSignature = (MethodSignature) signature;
			Method method = methodSignature.getMethod();
			if (method.isAnnotationPresent(ApiOperation.class)) {
				ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
				value = apiOperation.value();
			}

			return apiTags + "--" + value;
		} catch (Exception e) {
			LOG.error("记录接口名称出错, " + e.getMessage());
		}

		return "";
	}

	@Around("init()")
	public Object around(ProceedingJoinPoint pjp) {
		long startTime = System.currentTimeMillis();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();

		String url = request.getRequestURI();
		/**
		 * 设置用户信息到线程变量
		 */
		SysUser sysUser = new SysUser();
		sysUser.setTraceId(String.valueOf(IdMaker.get()));
		sysUser.setId(1L);
		sysUser.setName("admin");
		UserContext.set(sysUser);

		String apiName = this.getApiName(pjp);
		/**
		 * 获取url参数
		 */
		JSONObject urlArgs = this.urlArgs2String(request, request.getParameterNames());
		/**
		 * 获取body参数
		 */
		String bodyArgs = this.bodyArgs2String(request, pjp.getArgs());

		Object obj = null;
		try {
			obj = pjp.proceed();
			long endTime = System.currentTimeMillis();
			this.log2Kafka(request, url, urlArgs, bodyArgs, 0, "success", endTime - startTime, apiName);
		} catch (Throwable e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			if (e instanceof CustomException) {
				CustomException customEx = (CustomException) e;
				this.log2Kafka(request, url, urlArgs, bodyArgs, 400, customEx.getMsg(), endTime - startTime, apiName);
				throw customEx;
			}
			StringWriter stringWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(stringWriter));
			this.log2Kafka(request, url, urlArgs, bodyArgs, 500, stringWriter.toString(), endTime - startTime, apiName);
			throw new CustomException("系统错误, 请联系管理员");
		} finally {
			UserContext.remove();
		}

		return obj;
	}

	private void log2Kafka(HttpServletRequest request, String url, JSONObject urlArgs, String bodyArgs, Integer status,
			String message, long execTime, String apiName) {
		JSONObject json = new JSONObject();
		json.put("logType", "___rest___");
		if (UserContext.get() != null) {
			json.put("userName", UserContext.get().getLoginName());
			json.put(Constant.TRACE_ID, UserContext.get().getTraceId());
		}
		json.put("urlArgs", urlArgs);
		json.put("bodyArgs", bodyArgs);
		json.put("requestType", request.getMethod());
		json.put("timestamp", System.currentTimeMillis());
		json.put("visitTime", now2String());
		json.put("status", status);
		json.put("url", url);
		json.put("env", Constant.ENV);
		json.put("message", message);
		json.put("execTime", execTime);
		json.put("apiName", apiName);
		json.put("msgType", LogUtil.REST);

		LOG.info("\r\n" + json.toJSONString());
	}

	/**
	 * 获取当前时间
	 * 
	 * @author Todd Tan
	 * @date 2020年11月10日 下午4:13:12
	 * @return
	 */
	private static String now2String() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		return sdf.format(new Date());
	}
}
