package com.fpt.officelink.service;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Team;

public interface TeamService {
	
	Page<Team> searchWithPagination(String term, int pageNum);
	
	boolean addNewTeam(Team team);
	
	boolean modifyTeam(Team team);
	
	boolean removeTeam(int id);
}
