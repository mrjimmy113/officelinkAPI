package com.fpt.officelink.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Configuration;

public interface ConfigurationService {
	
	public List<Configuration> getConfigurations();
	
	Page<Configuration> getWithPagination(int workplaceId, int pageNum);
	
	public Configuration getConfigById(int configId);
	
	public boolean addNewConfig(Configuration config);
	
	public boolean modifyConfig(Configuration config);
	
	public boolean removeConfig(int id);
}
