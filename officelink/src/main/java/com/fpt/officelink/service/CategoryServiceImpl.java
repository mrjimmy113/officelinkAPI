package com.fpt.officelink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Category;
import com.fpt.officelink.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

	private static final int PAGEMAXSIZE = 10;
	
	@Autowired
	CategoryRepository rep;
	
	@Override
	public Page<Category> search(String name, int page) {
		PageRequest pageRequest = PageRequest.of(page - 1, PAGEMAXSIZE);
		return rep.findWithName(name, pageRequest);
	}
	
	@Override
	public void save(Category category) {
		rep.save(category);
	}
	
	
}
