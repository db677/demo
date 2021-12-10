package com.hiya3d.picturefix.conf.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义异常
 * 
 * @author Rex Tan
 * @date 2020年11月1日 上午11:35:46
 */
@Getter
@Setter
public class CustomException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private Integer code = 400;

	private String msg;

	public CustomException() {

	}

	public CustomException(String msg) {
		this.msg = msg;
	}

	public CustomException(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
