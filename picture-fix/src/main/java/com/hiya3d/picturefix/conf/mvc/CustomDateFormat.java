package com.hiya3d.picturefix.conf.mvc;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 全局日期格式转换
 * @author Rex Tan
 * @date 2020年11月1日 下午3:48:52
 */
public class CustomDateFormat extends DateFormat {
	private static final long serialVersionUID = 1L;
	/**
	 * yyyyMMdd
	 */
	private static final String YYYYMMDD = "^\\d{4}-\\d{2}-\\d{2}$";
	private static final String YYYYMMDD_FORMAT = "yyyy-MM-dd";
	private static final String YYYYMMDD2 = "^\\d{4}/\\d{2}/\\d{2}$";
	private static final String YYYYMMDD2_FORMAT = "yyyy/MM/dd";
	private static final String YYYYMMDD3 = "^\\d{4}\\d{2}\\d{2}$";
	private static final String YYYYMMDD3_FORMAT = "yyyyMMdd";
	private static final String YYYYMMDD4 = "^\\d{4}\\.\\d{2}\\.\\d{2}$";
	private static final String YYYYMMDD4_FORMAT = "yyyy.MM.dd";
	/**
	 * yyyyMMss HHmmss
	 */
	private static final String YYYYMMDDHHMMSS = "^\\d{4}-\\d{2}-\\d{2} \\d{2}-\\d{2}-\\d{2}$";
	private static final String YYYYMMDDHHMMSS_FORMAT = "yyyy-MM-dd HH-mm-ss";
	private static final String YYYYMMDDHHMMSS2 = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
	private static final String YYYYMMDDHHMMSS2_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String YYYYMMDDHHMMSS3 = "^\\d{4}/\\d{2}/\\d{2} \\d{2}/\\d{2}/\\d{2}$";
	private static final String YYYYMMDDHHMMSS3_FORMAT = "yyyy/MM/dd HH/mm/ss";
	private static final String YYYYMMDDHHMMSS4 = "^\\d{4}\\d{2}\\d{2} \\d{2}\\d{2}\\d{2}$";
	private static final String YYYYMMDDHHMMSS4_FORMAT = "yyyyMMdd HHmmss";
	private static final String YYYYMMDDHHMMSS5 = "^\\d{4}\\.\\d{2}\\.\\d{2} \\d{2}\\.\\d{2}\\.\\d{2}$";
	private static final String YYYYMMDDHHMMSS5_FORMAT = "yyyy.MM.dd HH.mm.ss";

	private DateFormat dateFormat;

	public CustomDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public Date parse(String source, ParsePosition pos) {
		if (StringUtils.isBlank(source)) {
			return null;
		}
		SimpleDateFormat sdf = getSimpleDateFormat(source);
		if (sdf == null) {
			return null;
		}

		return sdf.parse(source, pos);
	}

	/**
	 * 接收参数转换
	 */
	@Override
	public Date parse(String source) throws ParseException {
		if (StringUtils.isBlank(source)) {
			return null;
		}
		ParsePosition pos = new ParsePosition(0);
		SimpleDateFormat sdf = getSimpleDateFormat(source);
		if (sdf == null) {
			return null;
		}

		return sdf.parse(source, pos);
	}

	/**
	 * 返回数据日期格式转换
	 */
	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
		if (date == null) {
			return null;
		}
		// 如需返回其他格式, 在属性上添加注解 @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 设置时区
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String dateString = dateFormat.format(date);
		toAppendTo.append(dateString);

		return new StringBuffer(toAppendTo);
	}

	@Override
	public Object clone() {
		Object format = dateFormat.clone();
		return new CustomDateFormat((DateFormat) format);
	}

	private SimpleDateFormat getSimpleDateFormat(String source) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		if (Pattern.matches(YYYYMMDD, source)) {
			sdf = new SimpleDateFormat(YYYYMMDD_FORMAT);
		} else if (Pattern.matches(YYYYMMDDHHMMSS, source)) {
			sdf = new SimpleDateFormat(YYYYMMDDHHMMSS_FORMAT);
		} else if (Pattern.matches(YYYYMMDDHHMMSS2, source)) {
			sdf = new SimpleDateFormat(YYYYMMDDHHMMSS2_FORMAT);
		} else if (Pattern.matches(YYYYMMDD2, source)) {
			sdf = new SimpleDateFormat(YYYYMMDD2_FORMAT);
		} else if (Pattern.matches(YYYYMMDDHHMMSS3, source)) {
			sdf = new SimpleDateFormat(YYYYMMDDHHMMSS3_FORMAT);
		} else if (Pattern.matches(YYYYMMDD3, source)) {
			sdf = new SimpleDateFormat(YYYYMMDD3_FORMAT);
		} else if (Pattern.matches(YYYYMMDDHHMMSS4, source)) {
			sdf = new SimpleDateFormat(YYYYMMDDHHMMSS4_FORMAT);
		} else if (Pattern.matches(YYYYMMDD4, source)) {
			sdf = new SimpleDateFormat(YYYYMMDD4_FORMAT);
		} else if (Pattern.matches(YYYYMMDDHHMMSS5, source)) {
			sdf = new SimpleDateFormat(YYYYMMDDHHMMSS5_FORMAT);
		} else {
			return null;
		}
		return sdf;
	}

}
