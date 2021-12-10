package com.hiya3d.picturefix.k8s.vo.deployment;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * deployment信息
 * @author Rex.Tan
 * @date 2021-12-10 10:21:54
 */
@Data
public class DeploymentInfoVo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("创建时间")
	private String creationTimestamp;
	
	@ApiModelProperty("namespace")
	private String namespace;
	
	@ApiModelProperty("版本")
	private String resourceVersion;
	
	private String selfLink;
	
	@ApiModelProperty("副本数")
	private Integer replicas;
	
	@ApiModelProperty("strategyType")
	private String strategyType;
	
	@ApiModelProperty("容器信息")
	private List<ContainerInfoVo> containerList;
	
	@ApiModelProperty("conditions")
	private List<ConditionsInfoVo> conditionsList;
}
