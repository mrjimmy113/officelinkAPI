package com.fpt.officelink.service;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Team;

public interface TeamService {
	
	Page<Team> searchWithPagination(String term, int pageNum);
	
	void addNewTeam(Team team);
	
	void modifyTeam(Team team);
	
	void removeTeam(int id);
}
