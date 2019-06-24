package com.fpt.officelink.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Department;
import com.fpt.officelink.repository.DepartmentRepository;;

@Service
public class DepartmentServiceImpl implements DepartmentService{

	private static final int MAXPAGESIZE = 9;
	
	@Autowired
	DepartmentRepository depRep;
	
	public Department getDepartment(int depId) {
		Department result = depRep.getDepartmentWithTeam(depId);
		if (result == null) {
			Optional<Department> opDep = depRep.findById(depId);
			if (opDep.isPresent()) {
				result = opDep.get();
			}
		}
		
		return result; 
	}
	
	public List<Department> getAll() {
		List<Department> result = depRep.findAllByIsDeleted(false);
		return result;
	}
	
	@Override
	public Page<Department> searchWithPagination(String term, int pageNum) {
		if (pageNum > 0) {
			pageNum = pageNum - 1;
		}
		Pageable pageRequest = PageRequest.of(pageNum, MAXPAGESIZE);
		
		return depRep.findAllByNameContainingAndIsDeleted(term, false, pageRequest);
	}

	@Override
	public boolean addNewDepartment(Department dep) {
		Optional<Department> opDep = depRep.findByNameAndIsDeleted(dep.getName(), false);
		if (opDep.isPresent()) {
			return false;
		} else {
			dep.setDateCreated(new Date());
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
