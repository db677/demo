package com.hiya3d.picturefix.conf.user;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户表
 * @author Rex.Tan
 * @date 2021-一月-07 16:23:04:709
 */
@Data
@ApiModel("用户表")
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;
	
    @ApiModelProperty(value = "编号")
    private Long id;
    
    @ApiModelProperty(value = "登录名")
    private String loginName;
    
    @ApiModelProperty(value = "密码")
    private String password;
    
    @ApiModelProperty(value = "姓名")
    private String name;
    
    @ApiModelProperty(value = "邮箱")
    private String email;
    
    @ApiModelProperty(value = "电话")
    private String phone;
    
    @ApiModelProperty(value = "手机")
    private String mobile;
    
    @ApiModelProperty(value = "备注信息")
    private String remarks;
    
    private String traceId;
    
    @ApiModelProperty(value = "日志级别")
    private String logLevel;
}
