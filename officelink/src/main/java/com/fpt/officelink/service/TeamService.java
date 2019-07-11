package com.fpt.officelink.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Team;

public interface TeamService {
	
	Team getTeam(int id);
	
	List<Team> getTeamsByWorkplace(int workplaceId);
	
	Page<Team> searchWithPagination(String term, int workplaceId, int pageNum);
	
	boolean addNewTeam(Team team);
	
	boolean modifyTeam(Team team);
	
	boolean removeTeam(int id);

	List<Team> getTeamByDepartmentId(Integer id);


}
