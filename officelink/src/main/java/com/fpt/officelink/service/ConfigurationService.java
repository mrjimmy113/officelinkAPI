package com.fpt.officelink.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Configuration;
import com.fpt.officelink.entity.SurveySendTarget;

public interface ConfigurationService {
	
	public List<Configuration> getConfigurations();
	
	Page<Configuration> searchWithPagination(int workplaceId, String term, int pageNum);
	
	public Configuration getConfigById(int configId);
	
	
	public boolean removeConfig(int id);

	boolean addNewConfig(Configuration config, List<SurveySendTarget> targets);

	boolean modifyConfig(Configuration config, List<SurveySendTarget> targets);

	List<SurveySendTarget> filterDuplicate(List<SurveySendTarget> targets);
	
	void updateActiveStatus(int id, boolean isActive);
}
