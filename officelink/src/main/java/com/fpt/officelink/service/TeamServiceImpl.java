package com.fpt.officelink.service;

import java.util.Calendar;
import java.sql.Date;
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

		return teamRep.searchWithPaging(term, false, workplaceId, pageRequest);
	}

	@Override
	public boolean addNewTeam(Team team) {
		Optional<Team> opTeam = teamRep.findByNameAndIsDeleted(team.getName(), team.getDepartment().getWorkplace().getId() , false);
		if (opTeam.isPresent()) {
			return false;
		} else {
			team.setDateCreated(new Date(Calendar.getInstance().getTimeInMillis()));
			teamRep.save(team);
			return true;
		}
	}

	@Override
	public boolean modifyTeam(Team team) {
		Optional<Team> opTeam = teamRep.findByNameAndIsDeleted(team.getName(), team.getDepartment().getWorkplace().getId() , false);
		if (opTeam.isPresent()) {
			return false;
		} else {
			team.setDateModified(new Date(Calendar.getInstance().getTimeInMillis()));
			teamRep.save(team);
			return true;
		}
	}

	@Override
	public boolean removeTeam(int id, int workplaceId) {
		Team team = teamRep.findByIdAndWorkplaceId(id, workplaceId);
		if (team == null) {
			return false;
		}
		int count = team.getAccounts().size();
		if (count > 0) {
			return false;
		}
		
		team.setDeleted(true);
		team.setDateModified(new Date(Calendar.getInstance().getTimeInMillis()));
		teamRep.save(team);
		return true;
	}

	@Override
	public List<Team> getTeamsByWorkplace(int workplaceId) {
		return teamRep.findAllByWorkplaceId(workplaceId, false);
	}

}
