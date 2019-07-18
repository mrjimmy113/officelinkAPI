package com.fpt.officelink.dto;

import java.util.List;

public class DashBoardDTO {
	private int account;
	private int team;
	private int department;
	private List<LocationDTO> location;
	public int getAccount() {
		return account;
	}
	public void setAccount(int account) {
		this.account = account;
	}
	public int getTeam() {
		return team;
	}
	public void setTeam(int team) {
		this.team = team;
	}
	public int getDepartment() {
		return department;
	}
	public void setDepartment(int department) {
		this.department = department;
	}
	public List<LocationDTO> getLocation() {
		return location;
	}
	public void setLocation(List<LocationDTO> location) {
		this.location = location;
	}
	
	
}
