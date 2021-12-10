package com.hiya3d.picturefix.k8s.vo.namespace;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class NamespaceReq {

	@NotBlank(message = "name不能为空")
	private String name;
}
