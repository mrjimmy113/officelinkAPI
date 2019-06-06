package com.fpt.officelink.service;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Department;

public interface DepartmentService {
	
	Page<Department> searchWithPagination(String term, int pageNum);
	
	boolean addNewDepartment(Department dep);
	
	boolean modifyDepartment(Department dep);
	
	boolean removeDepartment(int id);
}
