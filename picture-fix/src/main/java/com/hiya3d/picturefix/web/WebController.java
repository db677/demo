package com.hiya3d.picturefix.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
public class WebController {

	@RequestMapping("/node")
	public String node(Model model) {
		return "node/index";
	}
	
	@RequestMapping("/deployment")
	public String deployment(Model model) {
		return "deployment/index";
	}
	
	@RequestMapping("/service")
	public String service(Model model) {
		return "service/index";
	}
	
	@RequestMapping("/ingress")
	public String ingress(Model model) {
		return "ingress/index";
	}
	
	@RequestMapping("/pod")
	public String pod(Model model) {
		return "pod/index";
	}
	
	@RequestMapping("/namespace")
	public String namespace(Model model) {
		return "namespace/index";
	}

}
