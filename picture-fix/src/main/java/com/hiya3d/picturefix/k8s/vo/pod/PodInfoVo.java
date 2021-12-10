package com.hiya3d.picturefix.k8s.vo.pod;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PodInfoVo implements Serializable {
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
	
	@ApiModelProperty("节点名称")
	private String nodeName;
	
	@ApiModelProperty("容器信息")
	private List<PodContainerVo> containers;

}
