package com.k8s.demo.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.k8s.demo.k8s.K8sUtil;
import com.k8s.demo.k8s.vo.namespace.NamespaceReq;
import com.k8s.demo.utils.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * namespace
 * 
 * @author Todd Tan
 * @date 2021-12-9 10:21:39
 */
@Api(tags = "namespace")
@RestController
public class NamespaceController {

	@ApiOperation("列表查询")
	@GetMapping("/namespace")
	public Result<?> find() {
		return new Result<>().data(K8sUtil.findNamespaceList());
	}

	@ApiOperation("添加")
	@PostMapping("/namespace")
	public Result<?> create(@Valid @RequestBody NamespaceReq namespaceReq) {
		K8sUtil.createNamespace(namespaceReq.getName());
		
		return Result.success();
	}

	@ApiOperation("删除")
	@DeleteMapping("/namespace/{name}")
	public Result<?> delete(@PathVariable("name") String name) {
		return new Result<>().data(K8sUtil.deleteNamespace(name));
	}
}
