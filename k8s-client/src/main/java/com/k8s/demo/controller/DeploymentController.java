package com.k8s.demo.controller;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.k8s.demo.k8s.K8sUtil;
import com.k8s.demo.k8s.vo.deployment.DeploymentReq;
import com.k8s.demo.k8s.vo.deployment.VolumeMountVo;
import com.k8s.demo.utils.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * deployment
 * 
 * @author Todd Tan
 * @date 2021-12-9 10:21:39
 */
@Api(tags = "deployment")
@RestController
public class DeploymentController {
	
	@ApiOperation("列表查询")
	@GetMapping("/deployment")
	public Result<?> find() {
		return new Result<>().data(K8sUtil.findDeployment("dev"));
	}

	@ApiOperation("创建")
	@PostMapping("/deployment/create")
	public Result<?> create(@RequestBody DeploymentReq req) {
		req.setContainerPort(9999);
		req.setHealthCheckPath("/k8s/health-check");
		req.setImage("10.0.12.10:30001/kubernetes/test_docker_img:1695191708764");
		req.setReplicas(1);
		req.setLimitCpu(new BigDecimal("0.5"));
		req.setLimitMemory(new BigDecimal("500"));
		req.setName("k8s-test");
		req.setResCpu(new BigDecimal("0.5"));
		req.setResMemory(new BigDecimal("500"));
		List<VolumeMountVo> mountList = new LinkedList<>();
		mountList.add(new VolumeMountVo("deploy-log", "/deploy/logs"));
		req.setVolumeMounts(mountList);
		req.setNamespace("dev");
		//
		K8sUtil.createDeployment(req);
		
		return Result.success();
	}
	
	@ApiOperation("修改")
	@PostMapping("/deployment/update")
	public Result<?> update(@RequestBody DeploymentReq req) {
		req.setContainerPort(9999);
		req.setHealthCheckPath("/k8s/health-check");
		req.setImage("10.0.12.10:30001/kubernetes/test_docker_img:1695191708764");
		req.setReplicas(1);
		req.setLimitCpu(new BigDecimal("0.6"));
		req.setLimitMemory(new BigDecimal("500"));
		req.setName("k8s-test");
		req.setResCpu(new BigDecimal("0.6"));
		req.setResMemory(new BigDecimal("500"));
		List<VolumeMountVo> mountList = new LinkedList<>();
		mountList.add(new VolumeMountVo("deploy-log", "/deploy/logs"));
		req.setVolumeMounts(mountList);
		req.setNamespace("dev");
		//
		K8sUtil.updateDeployment(req);
		
		return Result.success();
	}
	
	@ApiOperation("删除")
	@PostMapping("/deployment/delete")
	public Result<?> delete(@RequestBody DeploymentReq req) {
		req.setName("k8s-test");
		req.setNamespace("dev");
		
		return new Result<>().data(K8sUtil.deleteDeployment(req));
	}

}
