package com.hiya3d.picturefix.k8s.vo.deployment;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConditionsInfoVo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String lastTransitionTime;
	
	private String lastUpdateTime;
	
	private String message;
	
	private String reason;
	
	private String type;
	
	private String status;
}
