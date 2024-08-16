package org.xxx.demo;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping("test")
	public Object test(HttpServletRequest request) {
		return request.getServletContext().getSessionTimeout();
	}
	
	@GetMapping("/test1")
	public Object test1(HttpServletRequest request) {
		request.getSession().setAttribute("test", 1);
		return 1;
	}
	
	@GetMapping("/test2")
	public Object test2(HttpServletRequest request) {
		return request.getSession().getAttribute("test");
	}
	
	public static void main(String[] args) {
		System.out.println(BigDecimal.valueOf(Long.parseLong("1000.0")));
		
		System.out.println(new BigDecimal("1000.0"));
	}
}
