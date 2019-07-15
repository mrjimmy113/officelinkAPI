package com.fpt.officelink.dto;

import java.util.ArrayList;
import java.util.List;

public class SurveySendDetailDTO {
	
	private List<LocationDTO> locations = new ArrayList<LocationDTO>();
	
	private List<DepartmentDTO> departments = new ArrayList<DepartmentDTO>();
	
	private List<TeamDTO> teams = new ArrayList<TeamDTO>();

	public List<LocationDTO> getLocations() {
		return locations;
	}

	public void setLocations(List<LocationDTO> locations) {
		this.locations = locations;
	}

	public List<DepartmentDTO> getDepartments() {
		return departments;
	}

	public void setDepartments(List<DepartmentDTO> departments) {
		this.departments = departments;
	}

	public List<TeamDTO> getTeams() {
		return teams;
	}

	public void setTeams(List<TeamDTO> teams) {
		this.teams = teams;
	}
	
}
