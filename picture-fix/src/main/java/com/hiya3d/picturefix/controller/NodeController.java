package com.hiya3d.picturefix.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiya3d.picturefix.k8s.K8sUtil;
import com.hiya3d.picturefix.utils.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * node
 * 
 * @author Rex.Tan
 * @date 2021-12-9 10:21:39
 */
@Api(tags = "node")
@RestController
public class NodeController {

	@ApiOperation("列表查询")
	@GetMapping("/node")
	public Result<?> find() {
		return new Result<>().data(K8sUtil.findNodes());
	}

}
