package com.fpt.officelink.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpt.officelink.dto.CategoryDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.entity.Category;
import com.fpt.officelink.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

	@Autowired
	CategoryService ser;
	
	@Secured("ROLE_system_admin")
	@GetMapping
	public ResponseEntity<PageSearchDTO<CategoryDTO>> search(@RequestParam("term") String term, @RequestParam("page") int page) {
		HttpStatus status = null;

		PageSearchDTO<CategoryDTO> result = new PageSearchDTO<CategoryDTO>();
		try {
			Page<Category> res = ser.search(term, page);
			List<CategoryDTO> listDto = new ArrayList<CategoryDTO>();
			result.setMaxPage(res.getTotalPages());
			for (Category category : res.getContent()) {
				CategoryDTO dto = new CategoryDTO();
				BeanUtils.copyProperties(category, dto);
				listDto.add(dto);
			}
			result.setObjList(listDto);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<PageSearchDTO<CategoryDTO>>(result,status);
	}
	
	@Secured("ROLE_system_admin")
	@PostMapping
	public ResponseEntity<Void> create(@RequestBody CategoryDTO dto) {
		HttpStatus status = null;
		try {
			Category category = new Category();
			BeanUtils.copyProperties(dto, category);
			ser.save(category);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Void>(status);
	}
	
	@Secured("ROLE_system_admin")
	@PutMapping
	public ResponseEntity<Void> update(@RequestBody CategoryDTO dto) {
		HttpStatus status = null;
		try {
			Category category = new Category();
			BeanUtils.copyProperties(dto, category);
			ser.save(category);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Void>(status);
	}
	
}
