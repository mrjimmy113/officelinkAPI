package com.fpt.officelink.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Team;
import com.fpt.officelink.repository.TeamRepository;
import com.fpt.officelink.utils.Constants;

@Service
public class TeamServiceImpl implements TeamService {

	@Autowired
	TeamRepository teamRep;

	@Override
	public List<Team> getTeamByDepartmentId(Integer id) {
		return teamRep.findAllByDepartmentId(id);
	}



	@Override
	public Page<Team> searchWithPagination(String term, int workplaceId, int pageNum) {
		if (pageNum > 0) {
			pageNum = pageNum - 1;
		}
		PageRequest pageRequest = PageRequest.of(pageNum, Constants.MAX_PAGE_SIZE);

		return teamRep.findAllByNameContainingAndIsDeletedAndWorkplaceId(term, false, workplaceId, pageRequest);
	}

	@Override
	public boolean addNewTeam(Team team) {
		Optional<Team> opTeam = teamRep.findByNameAndIsDeleted(team.getName(), team.getDepartment().getWorkplace().getId() , false);
		if (opTeam.isPresent()) {
			return false;
		} else {
			team.setDateCreated(new Date());
			teamRep.save(team);
			return true;
		}
	}

	@Override
	public boolean modifyTeam(Team team) {
		team.setDateModified(new Date());
		teamRep.save(team);
		return true;
	}

	@Override
	public boolean removeTeam(int id) {
		Team team = teamRep.findById(id).get();
		if (team != null) {
			team.setDeleted(true);
		}
		
		team.setDateModified(new Date());
		teamRep.save(team);
		return true;
	}

	@Override
	public List<Team> getTeamsByWorkplace(int workplaceId) {
		return teamRep.findAllByWorkplaceId(workplaceId, false);
	}

	@Override
	public Team getTeam(int id) {
		Team team = null;
		Optional<Team> opTeams = teamRep.findById(id);
		
		if(opTeams.isPresent()) {
			team = opTeams.get();
		}
		return team;
	}
}
