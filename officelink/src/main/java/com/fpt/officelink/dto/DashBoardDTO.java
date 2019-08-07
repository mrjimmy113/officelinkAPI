package com.fpt.officelink.dto;

import java.util.List;

public class DashBoardDTO {
	private int account;
	private int team;
	private int department;
	private List<LocationDTO> location;
	private ImageNewsDTO news;
	private boolean endTutorial;
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
	public ImageNewsDTO getNews() {
		return news;
	}
	public void setNews(ImageNewsDTO news) {
		this.news = news;
	}
	public boolean isEndTutorial() {
		return endTutorial;
	}
	public void setEndTutorial(boolean endTutorial) {
		this.endTutorial = endTutorial;
	}
	
}
