package com.fpt.officelink.entity;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity
public class DepartmentInLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private Integer departmentId;
	
	private Integer locationId;
}
