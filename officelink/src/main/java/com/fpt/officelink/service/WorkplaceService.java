package com.fpt.officelink.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Workplace;

public interface WorkplaceService {
	
	List<Workplace> getAll();
	
	Page<Workplace> searchWithPagination(String term, int pageNum);
	
	boolean addNewWorkplace(Workplace workp);
	
	boolean modifyWorkplace(Workplace workp);
	
	boolean removeWorkplace(int id);
}
