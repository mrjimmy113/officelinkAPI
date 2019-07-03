package com.fpt.officelink.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class TeamDTO {
	
	@NotNull
	private int id;
	
	@NotEmpty
	private String name;
	
	@NotNull
	private DepartmentDTO department; 

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public DepartmentDTO getDepartment() {
		return department;
	}

	public void setDepartment(DepartmentDTO department) {
		this.department = department;
	}
}
