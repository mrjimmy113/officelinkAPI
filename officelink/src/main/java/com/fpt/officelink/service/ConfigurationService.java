package com.fpt.officelink.service;

import java.util.List;

import com.fpt.officelink.entity.Configuration;

public interface ConfigurationService {
	
	public void loadConfigurations();
	
	public List<Configuration> getConfigurations();
	
	public Configuration getConfigByWorkplaceId(int workplaceId);
	
}
