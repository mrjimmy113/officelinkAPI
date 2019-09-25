package com.fpt.officelink.service;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Category;

public interface CategoryService {

	Page<Category> search(String name, int page);

	void save(Category category);

}
