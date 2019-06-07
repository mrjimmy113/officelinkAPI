package com.fpt.officelink.dto;

import java.util.List;

public class DepartmentDTO {
	
	private Integer id;
	
	private String name;
	
	private List<TeamDTO> teams;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TeamDTO> getTeams() {
		return teams;
	}

	public void setTeams(List<TeamDTO> teams) {
		this.teams = teams;
	}
}
