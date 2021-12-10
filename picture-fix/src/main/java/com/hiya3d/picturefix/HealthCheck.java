package com.hiya3d.picturefix;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiya3d.picturefix.utils.Result;

/**
 * 探针接口
 * @author Rex.Tan
 * @date 2021-12-9 18:09:47
 */
@RestController
public class HealthCheck {

	@GetMapping("/health-check")
	public Result<?> healthCheck() {
		return Result.success();
	}
}
