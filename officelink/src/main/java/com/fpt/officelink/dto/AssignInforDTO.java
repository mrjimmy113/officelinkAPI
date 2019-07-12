package com.fpt.officelink.dto;

public class AssignInforDTO {
	private int accountId;
	private int locationId;
	private int[] teamIdList;
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public int[] getTeamIdList() {
		return teamIdList;
	}
	public void setTeamIdList(int[] teamIdList) {
		this.teamIdList = teamIdList;
	}
	
	
}
