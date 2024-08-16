package com.k8s.demo.k8s.vo.deployment;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ContainerInfoVo implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("镜像")
	private String image;
	
	@ApiModelProperty("containerPort")
	private String containerPort;
	
	@ApiModelProperty("内存")
	private Integer resMemory;
	
	@ApiModelProperty("CPU")
	private BigDecimal resCpu;
	
	@ApiModelProperty("最大内存")
	private Integer LimitMemory;
	
	@ApiModelProperty("最大CPU")
	private BigDecimal limitCpu;
}
