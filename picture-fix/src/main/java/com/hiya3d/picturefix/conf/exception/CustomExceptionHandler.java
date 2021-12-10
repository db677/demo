package com.hiya3d.picturefix.conf.exception;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.hiya3d.picturefix.conf.mvc.CustomDateFormat;
import com.hiya3d.picturefix.utils.Result;
import com.hiya3d.picturefix.utils.log.LogUtil;

/**
 * 全局异常捕获
 * 
 * @author Rex Tan
 * @date 2020年11月1日 下午12:15:00
 */
@RestControllerAdvice
public class CustomExceptionHandler {

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Object methodArgumentNotValidException(MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();
		if (bindingResult != null) {
			List<ObjectError> errors = bindingResult.getAllErrors();
			StringBuilder sb = new StringBuilder();
			for (ObjectError item : errors) {
				sb.append(item.getDefaultMessage() + ",");
			}
			String errMsg = sb.toString().replaceFirst("\\,$", "");
			return Result.error().msg(errMsg);
		}
		LogUtil.logException("参数解析错误: ", e);
		return new Result<>("参数解析错误");
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(CustomException.class)
	public Object customException(CustomException e) {
		return new Result<>(e.getCode(), e.getMsg());
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(Exception.class)
	public Object exception(Exception e) {
		e.printStackTrace();
		LogUtil.logException("未捕获异常: ", e);
		return Result.error().msg("系统错误, 请联系管理员");
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Object invalidFormatException(HttpMessageNotReadableException e) {
		if (e.getCause() instanceof InvalidFormatException) {
			InvalidFormatException invalidFmtEx = (InvalidFormatException) e.getCause();
			return Result.error().msg("参数格式错误: " + String.valueOf(invalidFmtEx.getValue()));
		}
		return Result.error().msg("参数格式错误");
	}

	/**
	 * 初始化绑定操作(get请求)
	 * 
	 * @author Rex Tan
	 * @date 2020年11月1日 下午4:00:45
	 * @param binder
	 */
	@InitBinder
	public void dataBind(WebDataBinder binder) {
		DateFormat fmt = new ObjectMapper().getDateFormat();
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new CustomDateFormat(fmt), true));
	}
}
