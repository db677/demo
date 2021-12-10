package com.hiya3d.picturefix.k8s.vo.service;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ServiceReq implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("名称")
	@NotBlank(message = "名称不能为空")
	private String name;
	
	@ApiModelProperty("namespace")
	@NotBlank(message = "namespace不能为空")
	private String namespace;
	
	@ApiModelProperty("容器端口")
	@NotNull(message = "容器端口不能为空")
	private Integer containerPort;
}
