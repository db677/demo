package com.k8s.demo.utils;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Result<T> implements Serializable {
	private static final Integer SUCCESS_CODE = 0;
	private static final Integer ERROR_CODE = 400;
	private static final String SUCCESS_MESSAGE = "操作成功";
	private static final String ERROR_MESSAGE = "操作失败";
	private static final long serialVersionUID = 1L;
	private Integer code = SUCCESS_CODE;

	private T data;

	@JsonInclude(Include.NON_NULL)
	private Integer total;

	private String msg;

	public static Result<?> success() {
		return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE);
	}

	public static Result<?> error() {
		return new Result<>(ERROR_CODE, ERROR_MESSAGE);
	}

	public Result() {
		this.code = SUCCESS_CODE;
		this.msg = SUCCESS_MESSAGE;
	}
	
	public Result(String msg) {
		this.code = SUCCESS_CODE;
		this.msg = msg;
	}

	public Result(T data) {
		this.code = SUCCESS_CODE;
		this.msg = SUCCESS_MESSAGE;
		this.data = data;
	}

	public Result(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public Result<?> msg(String msg) {
		this.msg = msg;
		return this;
	}
	
	public Result<?> data(T data) {
		this.data = data;

		return this;
	}

	public Result<T> total(Integer total) {
		this.total = total;

		return this;
	}

	public Result<T> total(Long total) {
		this.total = total != null ? total.intValue() : 0;

		return this;
	}
}
