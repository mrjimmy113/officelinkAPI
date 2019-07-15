package com.fpt.officelink.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Configuration;
import com.fpt.officelink.repository.ConfigurationRepository;
import com.fpt.officelink.utils.Constants;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
	
	@Autowired
	ConfigurationRepository configRep;
	
	@Autowired
	SchedulerService schedService;

	private List<Configuration> configurationList;
	
    /**
     * Get all configs regardless of workplace
     */
    @Override
    public List<Configuration> getConfigurations() {
    	List<Configuration> configs = configRep.findAllByIsDeleted(false);
    	return configs;
    }
    
    /**
     * Get config with paging for UI display
     */
    @Override
	public Page<Configuration> searchWithPagination(int workplaceId, String term, int pageNum) {
    	if (pageNum > 0) {
			pageNum = pageNum - 1;
		}
		Pageable pageRequest = PageRequest.of(pageNum, Constants.MAX_PAGE_SIZE);
		
		return configRep.searchBySurveyName(workplaceId, term, false, pageRequest);
	}

    public Configuration getConfigById(int configId) {
        return configurationList.get(configId);
    }

	@Override
	public boolean addNewConfig(Configuration config) {
		config.setDateCreated(new Date());
		configRep.save(config);
		// update schedule
		schedService.configureTasks(new ScheduledTaskRegistrar());
		
		return true;
	}

	@Override
	public boolean modifyConfig(Configuration config) {
		config.setDateModified(new Date());
		configRep.save(config);
		// update schedule
		schedService.configureTasks(new ScheduledTaskRegistrar());
		
		return true;
	}

	@Override
	public boolean removeConfig(int id) {
		Configuration config = configRep.findById(id).get();
		if (config == null) {
			return false;
		}
		
		config.setDateModified(new Date());
		config.setDeleted(true);
		configRep.save(config);
		// update schedule
		schedService.configureTasks(new ScheduledTaskRegistrar());
		
		return true;
	}
}
