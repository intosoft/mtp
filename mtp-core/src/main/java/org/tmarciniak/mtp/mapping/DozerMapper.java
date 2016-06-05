package org.tmarciniak.mtp.mapping;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Component;

@Component
public class DozerMapper extends DozerBeanMapper {

	@PostConstruct
	public void init() {
		setMappingFiles(Arrays
				.asList(new String[] { "dozer/dozerBeanMapping.xml" }));
	}

}
