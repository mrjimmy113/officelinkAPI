package com.fpt.officelink.service;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Department;

public interface DepartmentService {
	
	Page<Department> searchWithPagination(String term, int pageNum);
	
	void addNewDepartment(Department dep);
	
	void modifyDepartment(Department dep);
	
	void removeDepartment(int id);
}
