package com.hiya3d.picturefix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiya3d.picturefix.jvm.JvmUtil;
import com.hiya3d.picturefix.utils.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * jvm
 * @author Rex.Tan
 * @date 2021-12-13 14:37:42
 */
@Api(tags = "jvm")
@RestController
public class JvmController {
	@Autowired
	JvmUtil jvmUtil;
	
	@ApiOperation("env")
	@GetMapping("/jvm/env")
	public Result<?> env() {
		return new Result<>().data(jvmUtil.env());
	}
	
	@ApiOperation("mem")
	@GetMapping("/jvm/mem")
	public Result<?> mem() {
		return new Result<>().data(jvmUtil.mem());
	}
	
	@ApiOperation("cpu")
	@GetMapping("/jvm/cpu")
	public Result<?> cpu() {
		return new Result<>().data(jvmUtil.cpu());
	}
	
	@ApiOperation("jvm")
	@GetMapping("/jvm/jvm")
	public Result<?> jvm() {
		return new Result<>().data(jvmUtil.jvm());
	}
}
