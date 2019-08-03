package com.fpt.officelink.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Configuration;
import com.fpt.officelink.entity.Department;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveySendTarget;
import com.fpt.officelink.entity.Team;
import com.fpt.officelink.repository.ConfigurationRepository;
import com.fpt.officelink.repository.SurveyRepository;
import com.fpt.officelink.repository.SurveySendTargetRepository;
import com.fpt.officelink.utils.Constants;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired
	ConfigurationRepository configRep;

	@Autowired
	SchedulerService schedService;

	@Autowired
	SurveySendTargetRepository targetRep;
	
	@Autowired
	SurveyRepository surRep;

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
	public boolean addNewConfig(Configuration config, List<SurveySendTarget> targets) {
		Optional<Survey> opSur = surRep.findById(config.getSurvey().getId());
		if(opSur.isPresent()) {
			Survey tmp = opSur.get();
			tmp.setSent(true);
			surRep.save(tmp);
		}
		targets = filterDuplicate(targets);
		targetRep.saveAll(targets);

		config.setDateCreated(new Date());
		configRep.save(config);

		// update schedule
		schedService.configureTasks(new ScheduledTaskRegistrar());

		return true;
	}

	@Transactional
	@Override
	public boolean modifyConfig(Configuration config, List<SurveySendTarget> targets) {
		config.setDateModified(new Date());
		configRep.save(config);

		targetRep.deleteBySurveyId(config.getSurvey().getId());
		targets = filterDuplicate(targets);
		targetRep.saveAll(targets);
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

	@Override
	public List<SurveySendTarget> filterDuplicate(List<SurveySendTarget> targets) {
		List<SurveySendTarget> locations = new ArrayList<SurveySendTarget>();
		List<SurveySendTarget> deps = new ArrayList<SurveySendTarget>();
		List<SurveySendTarget> locationDeps = new ArrayList<SurveySendTarget>();
		List<SurveySendTarget> teams = new ArrayList<SurveySendTarget>();
		List<SurveySendTarget> filtered = new ArrayList<SurveySendTarget>();
		boolean isAll = false;
		for (int i = 0; i < targets.size(); i++) {
			Location location = targets.get(i).getLocation();
			Department dep = targets.get(i).getDepartment();
			Team team = targets.get(i).getTeam();
			if (location == null && dep == null && team == null) {
				isAll = true;
				filtered.addAll(targets);
				filtered.get(i).setNeed(true);
				break;
			}
		}
		if (!isAll) {
			for (SurveySendTarget target : targets) {
				Location location = target.getLocation();
				Department dep = target.getDepartment();
				Team team = target.getTeam();
				target.setNeed(true);
				if (location != null && dep == null && team == null) {
					locations.add(target);
				}
				if (location == null && dep != null && team == null) {
					deps.add(target);
				}
				if (location != null && dep != null && team == null) {
					locationDeps.add(target);
				}
				if (location != null && dep != null && team != null) {
					teams.add(target);
				}
			}
			for (SurveySendTarget locationDep : locationDeps) {
				for (SurveySendTarget location : locations) {
					if (locationDep.getLocation().equals(location.getLocation())) {
						locationDep.setNeed(false);
						break;
					}
				}
				for (SurveySendTarget dep : deps) {
					if (locationDep.getLocation().equals(dep.getLocation())) {
						locationDep.setNeed(false);
						break;
					}
				}
			}
			for (SurveySendTarget team : teams) {
				for (SurveySendTarget location : locations) {
					if (team.getLocation().equals(location.getLocation())) {
						team.setNeed(false);
						break;
					}
				}
				for (SurveySendTarget dep : deps) {
					if (team.getLocation().equals(dep.getLocation())) {
						team.setNeed(false);
						break;
					}
				}
				for (SurveySendTarget locationDep : locationDeps) {
					if (team.getLocation().equals(locationDep.getLocation())
							&& team.getDepartment().equals(locationDep.getDepartment())) {
						team.setNeed(false);
						break;
					}
				}
			}

		}
		filtered.addAll(locations);
		filtered.addAll(deps);
		filtered.addAll(locationDeps);
		filtered.addAll(teams);
		return filtered;
	}
}
