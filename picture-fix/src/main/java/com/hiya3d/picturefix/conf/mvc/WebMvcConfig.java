package com.hiya3d.picturefix.conf.mvc;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * MVC配置
 * 
 * @author Rex Tan
 * @date 2020年11月1日 下午3:48:59
 */
@SuppressWarnings("deprecation")
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**")//
				.addResourceLocations("classpath:/static/");
	}

	/**
	 * 为true 路径参数如果带"."， 则后面的值将被忽略 默认为true
	 */
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseSuffixPatternMatch(false);
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(false);
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(mappingJackson2HttpMessageConverter());
	}

	/**
	 * 配置全局日期格式转换
	 */
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(0, mappingJackson2HttpMessageConverter());
	}

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();

		// json中多余的参数不报错
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 设置全局的时间转化
		DateFormat dateFormat = objectMapper.getDateFormat();
		objectMapper.setDateFormat(new CustomDateFormat(dateFormat));

		// 设置中文编码格式
		List<MediaType> list = new ArrayList<MediaType>();
		list.add(MediaType.APPLICATION_JSON_UTF8);
		jackson2HttpMessageConverter.setSupportedMediaTypes(list);

		jackson2HttpMessageConverter.setObjectMapper(objectMapper);

		return jackson2HttpMessageConverter;
	}

}
