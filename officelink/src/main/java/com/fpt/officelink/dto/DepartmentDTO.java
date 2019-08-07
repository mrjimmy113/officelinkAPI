package com.fpt.officelink.dto;

import java.sql.Date;
import java.util.List;

public class DepartmentDTO {
	
	private Integer id;
	
	private String name;
	
	private Date dateCreated;
	
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

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
//	public List<LocationDTO> getLocations() {
//		return locations;
//	}
//
//	public void setLocations(List<LocationDTO> locations) {
//		this.locations = locations;
//	}
}
