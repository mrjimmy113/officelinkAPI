package com.fpt.officelink.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Configuration;
import com.fpt.officelink.repository.ConfigRepository;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
	
	ConfigRepository configRepository;

	private Map<Integer, Configuration> configurationList;
	
	@Autowired
    public ConfigurationServiceImpl(ConfigRepository configRepository) {
        this.configRepository = configRepository;
        this.configurationList = new ConcurrentHashMap<>();
    }
	
	/**
     * Loads configuration parameters from Database
     */
    @PostConstruct
    public void loadConfigurations() {
        List<Configuration> configs = configRepository.findAllByIsDeleted(false);
        for (Configuration configuration : configs) {
            this.configurationList.put(configuration.getWorkplace().getId(), configuration);
        }
    }
    
    public List<Configuration> getConfigurations() {
    	List<Configuration> configs = configRepository.findAll();
    	return configs;
    }

    public Configuration getConfigByWorkplaceId(int workplaceId) {
        return configurationList.get(workplaceId);
    }

}
