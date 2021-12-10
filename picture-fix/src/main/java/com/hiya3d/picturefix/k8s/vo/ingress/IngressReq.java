package com.hiya3d.picturefix.k8s.vo.ingress;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IngressReq implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("名称")
	@NotBlank(message = "名称不能为空")
	private String name;
	
	@ApiModelProperty("namespace")
	@NotBlank(message = "namespace不能为空")
	private String namespace;
	
	private Integer containerPort;
}
