package com.hiya3d.picturefix.k8s.vo.pod;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.hiya3d.picturefix.k8s.vo.deployment.VolumeMountVo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PodContainerVo implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("镜像")
	private String image;
	
	@ApiModelProperty("内存")
	private Integer resMemory;
	
	@ApiModelProperty("CPU")
	private BigDecimal resCpu;
	
	@ApiModelProperty("最大内存")
	private Integer LimitMemory;
	
	@ApiModelProperty("最大CPU")
	private BigDecimal limitCpu;
	
	@ApiModelProperty("端口")
	private List<PodPortVo> ports;
	
	@ApiModelProperty("挂载目录")
	private List<VolumeMountVo> volumeMounts;
}
