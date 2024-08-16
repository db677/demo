package com.k8s.demo.k8s.vo.pod;

import java.io.Serializable;

import lombok.Data;

@Data
public class PodStatusVo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String image;
	
	private Boolean ready;
	
	private String name;
	
	private Integer restartCount;
	
	private Boolean started;
	
	private String message;
	
	private String reason;
}
