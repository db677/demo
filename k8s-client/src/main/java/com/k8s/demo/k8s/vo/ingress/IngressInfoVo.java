package com.k8s.demo.k8s.vo.ingress;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IngressInfoVo implements Serializable {
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
	
}
