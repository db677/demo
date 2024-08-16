package com.k8s.demo.k8s.vo.deployment;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeploymentReq {
	
	@ApiModelProperty("名称")
	@NotBlank(message = "名称不能为空")
	private String name;
	
	@ApiModelProperty("namespace")
	@NotBlank(message = "namespace不能为空")
	private String namespace;
	
	@ApiModelProperty("副本数")
	@NotNull(message = "副本数不能为空")
	private Integer replicas;
	
	@ApiModelProperty("镜像")
	@NotBlank(message = "镜像不能为空")
	private String image;
	
	@ApiModelProperty("容器端口")
	@NotNull(message = "容器端口不能为空")
	private Integer containerPort;
	
	@ApiModelProperty("健康检查接口地址")
	@NotBlank(message = "健康检查接口地址不能为空")
	private String healthCheckPath;
	
	@ApiModelProperty("内存")
	@NotNull(message = "内存不能为空")
	private BigDecimal resMemory;
	
	@ApiModelProperty("CPU")
	@NotNull(message = "CPU不能为空")
	private BigDecimal resCpu;
	
	@ApiModelProperty("最大内存")
	@NotNull(message = "最大内存不能为空")
	private BigDecimal LimitMemory;
	
	@ApiModelProperty("最大CPU")
	@NotNull(message = "最大CPU不能为空")
	private BigDecimal limitCpu;
	
	@ApiModelProperty("挂载目录")
	private List<VolumeMountVo> volumeMounts;
}
