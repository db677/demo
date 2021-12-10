package com.hiya3d.picturefix.k8s.vo.namespace;

import java.io.Serializable;

import lombok.Data;

@Data
public class NamespaceInfoVo implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 创建时间
	 */
	private String creationTimestamp;

	private String name;

	private String resourceVersion;

	private String selfLink;

	private String uid;

	private String phase;
}
