package com.fpt.officelink.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class WorkplaceDTO {
	
	@NotNull
	private Integer id;
	
	@NotEmpty
	private String name;
	
	
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

}
