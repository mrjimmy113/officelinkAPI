package com.fpt.officelink.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Department;
import com.fpt.officelink.repository.DepartmentRepository;
import com.fpt.officelink.utils.Constants;;

@Service
public class DepartmentServiceImpl implements DepartmentService{
	
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
	
	public List<Department> getAllByWorkplace(int workplaceId) {
		List<Department> result = depRep.findAllByWorkplaceIdAndIsDeleted(workplaceId, false);
		return result;
	}

	@Override
	public Page<Department> searchWithPagination(String term, int workplaceId, int pageNum) {
		if (pageNum > 0) {
			pageNum = pageNum - 1;
		}
		Pageable pageRequest = PageRequest.of(pageNum, Constants.MAX_PAGE_SIZE);
		
		return depRep.findAllByNameContainingAndIsDeletedAndWorkplaceId(term, false, workplaceId, pageRequest);
	}

	@Override
	public boolean addNewDepartment(Department dep) {
		Optional<Department> opDep = depRep.findByNameAndIsDeletedAndWorkplaceId(dep.getName(), false, dep.getWorkplace().getId());
		if (opDep.isPresent()) {
			return false;
		} else {
			dep.setDateCreated(new Date(Calendar.getInstance().getTimeInMillis()));
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
	public boolean removeDepartment(int id, int workplaceId) {
		Department dep = depRep.findByIdAndWorkplaceId(id, workplaceId);
		if (dep == null) {
			return false;
		}
		int count = depRep.countTeamsInDep(id);
		if (count > 0) {
			return false;
		}
		
		dep.setDateModified(new Date(Calendar.getInstance().getTimeInMillis()));
		dep.setDeleted(true);
		depRep.save(dep);
		
		return true;
	}
	
	@Override
	public List<Department> getByLocationId(int id) {
		return new ArrayList<Department>(depRep.findAllByLocationId(id));
	}

}
