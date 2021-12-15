package com.hiya3d.picturefix.k8s.vo.node;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NodeInfoVo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty("名称")
	private String name;

	@ApiModelProperty("架构")
	private String architecture;

	@ApiModelProperty("容器版本")
	private String containerRuntimeVersion;

	@ApiModelProperty("内核版本")
	private String kernelVersion;

	@ApiModelProperty("k8s版本")
	private String kubeletVersion;

	private String kubeProxyVersion;

	@ApiModelProperty("操作系统")
	private String operatingSystem;

	@ApiModelProperty("系统镜像")
	private String osImage;
	
	private BigDecimal allocatableCpu;
	
	private BigDecimal allocatableMemory;
	
	private BigDecimal allocatablePods;
	
	private BigDecimal capacityCpu;
	
	private BigDecimal capacityMemory;
	
	private BigDecimal capacityPods; 
	
	@ApiModelProperty("创建时间")
	private String creationTimestamp;
	
	private List<NodeConditionVo> conditions;

}
