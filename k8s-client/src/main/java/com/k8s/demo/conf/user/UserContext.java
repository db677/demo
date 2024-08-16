package com.k8s.demo.conf.user;

/**
 * 用户上下文
 * @author Todd Tan
 * @date 2020年11月1日 下午1:14:21
 */
public class UserContext {
	private static final ThreadLocal<SysUser> CONTEXT = new InheritableThreadLocal<>();
	
	public static void set(SysUser user) {
		CONTEXT.set(user);
	}
	
	public static SysUser get() {
		return CONTEXT.get();
	}
	
	public static void remove() {
		CONTEXT.remove();
	}
}
