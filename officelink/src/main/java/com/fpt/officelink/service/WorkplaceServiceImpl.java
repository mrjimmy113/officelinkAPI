package com.fpt.officelink.service;

import java.util.Calendar;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.repository.WorkplaceRepository;
import com.fpt.officelink.utils.Constants;;

@Service
public class WorkplaceServiceImpl implements WorkplaceService {

	@Autowired
	WorkplaceRepository workpRep;

	public List<Workplace> getAll() {
		List<Workplace> result = workpRep.findAllByIsDeleted(false);
		return result;
	}

	@Override
	public Workplace getWorkplace(int workplaceId) {
		Optional<Workplace> opWorkplace = workpRep.findById(workplaceId);
		
		if (opWorkplace.isPresent()) {
			return opWorkplace.get();
		}
		
		return null;
	}

	@Override
	public Page<Workplace> searchWithPagination(String term, int pageNum) {
		if (pageNum > 0) {
			pageNum = pageNum - 1;
		}
		Pageable pageRequest = PageRequest.of(pageNum, Constants.MAX_PAGE_SIZE);

		return workpRep.searchWithPaging(term, pageRequest);
	}

	@Override
	public boolean addNewWorkplace(Workplace workp) {
		Optional<Workplace> opDep = workpRep.findByNameAndIsDeleted(workp.getName(), false);
		if (opDep.isPresent()) {
			return false;
		} else {
			Date date = new Date(Calendar.getInstance().getTimeInMillis());
			workp.setDateCreated(date);
			workp.setDateModified(date);
			workpRep.save(workp);
			return true;
		}
	}

	@Override
	public boolean modifyWorkplace(Workplace workp) {
		Optional<Workplace> opDep = workpRep.findByNameAndIsDeleted(workp.getName(), false);
		if (opDep.isPresent()) {
			return false;
		} else {
			workp.setDateModified(new Date(Calendar.getInstance().getTimeInMillis()));
			workpRep.save(workp);
			return true;
		}
	}

	@Override
	public boolean removeWorkplace(int id) {
		Workplace workp = workpRep.findById(id).get();
		if (workp == null) {
			return false;
		}

		workp.setDateModified(new Date(Calendar.getInstance().getTimeInMillis()));
		workp.setDeleted(true);
		workpRep.save(workp);
		return true;
	}

	@Override
	public boolean changeActiveStatus(int id, boolean status) {
		Workplace workp = workpRep.findById(id).get();
		if (workp == null) {
			return false;
		}
		
		workp.setDateModified(new Date(Calendar.getInstance().getTimeInMillis()));
		workp.setDeleted(status);
		workpRep.save(workp);
		
		return true;
	}
}
