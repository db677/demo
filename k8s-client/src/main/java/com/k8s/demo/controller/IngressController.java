package com.k8s.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.k8s.demo.k8s.K8sUtil;
import com.k8s.demo.k8s.vo.ingress.IngressReq;
import com.k8s.demo.utils.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * ingress
 * 
 * @author Todd Tan
 * @date 2021-12-9 10:21:39
 */
@Api(tags = "ingress")
@RestController
public class IngressController {

	@ApiOperation("列表查询")
	@GetMapping("/ingress")
	public Result<?> find() {
		return new Result<>().data(K8sUtil.findIngress("dev"));
	}
	
	@ApiOperation("添加")
	@PostMapping("/ingress/create")
	public Result<?> create(@RequestBody IngressReq req) {
		req.setContainerPort(9999);
		req.setName("k8s-test");
		req.setNamespace("dev");
		req.setHost("hub.test.com");
		req.setPath("/k8s");
		K8sUtil.createIngress(req);
		
		return Result.success();
	}
	
	@ApiOperation("更新")
	@PostMapping("/ingress/update")
	public Result<?> update(@RequestBody IngressReq req) {
		req.setContainerPort(9999);
		req.setName("k8s-test");
		req.setNamespace("dev");
		req.setHost("hub.test.com");
		req.setPath("/k8s");
		K8sUtil.updateIngress(req);
		
		return Result.success();
	}
	
	@ApiOperation("删除")
	@PostMapping("/ingress/delete")
	public Result<?> delete(@RequestBody IngressReq req) {
		req.setName("k8s-test");
		req.setNamespace("dev");
		K8sUtil.deleteIngress(req);
		
		return Result.success();
	}

}
