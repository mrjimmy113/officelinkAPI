package com.fpt.officelink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Team;
import com.fpt.officelink.repository.TeamRepository;

@Service
public class TeamServiceImpl implements TeamService{

	private static final int PAGEMAXSIZE = 9;
	
	@Autowired
	TeamRepository teamRep;
	
	@Override
	public Page<Team> searchWithPagination(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return teamRep.findAllByNameContaining(term, pageRequest);
	}

	@Override
	public void addNewTeam(Team team) {
		teamRep.save(team);
	}

	@Override
	public void modifyTeam(Team team) {
		teamRep.save(team);
	}

	@Override
	public void removeTeam(int id) {
		Team team = teamRep.findById(id).get();
		if (team != null) {
			team.setDeleted(true);
		}
		teamRep.save(team);
	}
	
}
