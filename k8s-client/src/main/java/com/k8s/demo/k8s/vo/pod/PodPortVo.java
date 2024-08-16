package com.k8s.demo.k8s.vo.pod;

import java.io.Serializable;

import lombok.Data;

@Data
public class PodPortVo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer containerPort;

	public PodPortVo() {

	}

	public PodPortVo(Integer containerPort) {
		this.containerPort = containerPort;
	}
}
