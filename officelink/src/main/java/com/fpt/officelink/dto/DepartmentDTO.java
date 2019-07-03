package com.fpt.officelink.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DepartmentDTO {
	
	@NotNull
	private Integer id;
	
	@NotEmpty
	private String name;
	
	@NotNull
	private List<TeamDTO> teams;
	
//	private List<LocationDTO> locations;
	
	//Getter setter	
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
	
//	public List<LocationDTO> getLocations() {
//		return locations;
//	}
//
//	public void setLocations(List<LocationDTO> locations) {
//		this.locations = locations;
//	}
}
