package com.k8s.demo.k8s.vo.node;

import java.io.Serializable;

import lombok.Data;

@Data
public class NodeConditionVo  implements Serializable{
	private static final long serialVersionUID = 1L;

	private String type;
	
	private String status;
	
	private String message;
	
	private String reason;
}
