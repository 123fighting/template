/*
 * Timeloit.com Inc.
 * Copyright (c) 2012 时代凌宇物联网数据平台. All Rights Reserved
 */
package com.template.projectName;

import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * 将String转化成CommonsMultipartFile类型
 * 
 */
public class StringToCommonsMultipartFileConverter implements
		Converter<String, CommonsMultipartFile> {

	@Override
	public CommonsMultipartFile convert(String arg0) {
		return null;
	}

}