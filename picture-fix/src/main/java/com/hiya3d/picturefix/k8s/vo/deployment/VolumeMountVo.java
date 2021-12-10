package com.hiya3d.picturefix.k8s.vo.deployment;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 挂载目录
 * 
 * @author Rex.Tan
 * @date 2021-12-9 16:20:13
 */
@ApiModel("挂载目录")
@Data
public class VolumeMountVo {

	@ApiModelProperty("挂载目录")
	@NotBlank(message = "挂载目录不能为空")
	private String mountPath;

	@ApiModelProperty("名称")
	@NotBlank(message = "名称")
	private String name;

	public VolumeMountVo() {

	}

	public VolumeMountVo(String name, String mountPath) {
		this.name = name;
		this.mountPath = mountPath;
	}
}
