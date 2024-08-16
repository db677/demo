package com.k8s.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.k8s.demo.k8s.K8sUtil;
import com.k8s.demo.utils.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * service
 * 
 * @author Todd Tan
 * @date 2021-12-9 10:21:39
 */
@Api(tags = "pod")
@RestController
public class PodController {

	@ApiOperation("列表查询")
	@GetMapping("/pod")
	public Result<?> find() {
		return new Result<>().data(K8sUtil.findPod("dev"));
	}
	
	@ApiOperation("列表查询")
	@GetMapping("/pod/get")
	public Result<?> get() {
		return new Result<>().data(K8sUtil.readPod("picture-fix-6dc4476f98-csb6h", "dev"));
	}
}
