package com.fpt.officelink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Team;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.repository.TeamRepository;
import com.fpt.officelink.repository.WorkplaceRepository;

@Service
public class SystemBot {

	@Autowired
	private TeamRepository teamRep;
	
	@Autowired
	private WorkplaceRepository workplaceRep;

	@Async
	public void printTeam(int id) {
		Team team = teamRep.findById(id).get();

		System.out.println("\n" + team.getId() + "||" + team.getName() + "\n");
	}

	@Async
	public void printWorkplace(int id) {
		Workplace workplace = workplaceRep.findById(id).get();

		System.out.println("\n" + workplace.getId() + "||" + workplace.getName() + "\n");
	}
	
}
