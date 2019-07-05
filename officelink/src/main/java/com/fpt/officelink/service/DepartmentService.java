package com.fpt.officelink.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Department;

public interface DepartmentService {
	
	Department getDepartment(int depId);
	
	List<Department> getAll(int workplaceId);
	
	Page<Department> searchWithPagination(String term, int workplaceId, int pageNum);
	
	boolean addNewDepartment(Department dep);
	
	boolean modifyDepartment(Department dep);
	
	boolean removeDepartment(int id);
}
