package com.k8s.demo.k8s.vo.service;

import java.io.Serializable;

import lombok.Data;

@Data
public class PortVo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer port;

	private Integer nodePort;

	private Integer targetPort;

	public PortVo() {

	}

	public PortVo(Integer port, Integer nodePort, Integer targetPort) {
		this.port = port;
		this.nodePort = nodePort;
		this.targetPort = targetPort;
	}
}
