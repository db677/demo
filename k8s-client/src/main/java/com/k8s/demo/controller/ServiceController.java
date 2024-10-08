package com.k8s.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.k8s.demo.k8s.K8sUtil;
import com.k8s.demo.k8s.vo.service.ServiceReq;
import com.k8s.demo.utils.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * service
 * 
 * @author Todd Tan
 * @date 2021-12-9 10:21:39
 */
@Api(tags = "service")
@RestController
public class ServiceController {

	@ApiOperation("列表查询")
	@GetMapping("/service")
	public Result<?> find() {
		return new Result<>().data(K8sUtil.findService("dev"));
	}
	
	@ApiOperation("添加")
	@PostMapping("/service/create")
	public Result<?> create(@RequestBody ServiceReq req) {
		req.setContainerPort(9999);
		req.setName("k8s-test");
		req.setNamespace("dev");
		K8sUtil.createService(req);
		
		return Result.success();
	}
	
	@ApiOperation("更新")
	@PostMapping("/service/update")
	public Result<?> update(@RequestBody ServiceReq req) {
		req.setContainerPort(9999);
		req.setName("k8s-test");
		req.setNamespace("dev");
		K8sUtil.updateService(req);
		
		return Result.success();
	}
	
	@ApiOperation("删除")
	@PostMapping("/service/delete")
	public Result<?> delete(@RequestBody ServiceReq req) {
		req.setName("k8s-test");
		req.setNamespace("dev");
		K8sUtil.deleteService(req);
		
		return Result.success();
	}

}
