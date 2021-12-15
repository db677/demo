package com.hiya3d.picturefix.k8s.vo.pod;

import java.io.Serializable;

import lombok.Data;

@Data
public class PodConditionVo  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String type;
	
	private String message;
	
	private String reason;
	
	private String status;
}
