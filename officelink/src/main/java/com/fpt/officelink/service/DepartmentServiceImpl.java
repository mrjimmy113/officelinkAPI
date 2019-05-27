package com.fpt.officelink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Department;
import com.fpt.officelink.repository.DepartmentRespository;

@Service
public class DepartmentServiceImpl implements DepartmentService{

	private static final int MAXPAGESIZE = 9;
	
	@Autowired
	DepartmentRespository depRep;
	
	@Override
	public Page<Department> searchWithPagination(String term, int pageNum) {
		Pageable pageRequest = PageRequest.of(pageNum, MAXPAGESIZE);
		
		return depRep.findAllByNameContaining(term, pageRequest);
	}

	@Override
	public void addNewDepartment(Department dep) {
		depRep.save(dep);
	}

	@Override
	public void modifyDepartment(Department dep) {
		depRep.save(dep);
		
	}

	@Override
	public void removeDepartment(int id) {
		Department dep = depRep.findById(id).get();
		if (dep != null) {
			dep.setDeleted(true);
			depRep.save(dep);
		}
	}

}
