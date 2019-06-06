package com.fpt.officelink.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Department;
import com.fpt.officelink.entity.Team;
import com.fpt.officelink.repository.DepartmentRespository;

@Service
public class DepartmentServiceImpl implements DepartmentService{

	private static final int MAXPAGESIZE = 9;
	
	@Autowired
	DepartmentRespository depRep;
	
	@Override
	public Page<Department> searchWithPagination(String term, int pageNum) {
		Pageable pageRequest = PageRequest.of(pageNum, MAXPAGESIZE);
		
		return depRep.findAllByNameContainingAndIsDeleted(term, false, pageRequest);
	}

	@Override
	public boolean addNewDepartment(Department dep) {
		Optional<Department> opDep = depRep.findByNameAndIsDeleted(dep.getName(), false);
		if (opDep.isPresent()) {
			return false;
		} else {
			depRep.save(dep);
			return true;
		}
	}

	@Override
	public boolean modifyDepartment(Department dep) {
		depRep.save(dep);
		return true;
	}

	@Override
	public boolean removeDepartment(int id) {
		Department dep = depRep.findById(id).get();
		if (dep != null) {
			dep.setDeleted(true);
			depRep.save(dep);
		}
		return true;
	}

}
