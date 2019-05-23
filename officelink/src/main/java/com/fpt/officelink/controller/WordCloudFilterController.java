package com.fpt.officelink.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.dto.WordCloudFilterDTO;
import com.fpt.officelink.entity.WordCloudFilter;
import com.fpt.officelink.service.WordCloudFilterService;

@RestController
@RequestMapping("/wordCloud")
public class WordCloudFilterController {

	@Autowired
	WordCloudFilterService service;
	
	
	@GetMapping
	public ResponseEntity<PageSearchDTO<WordCloudFilterDTO>> search(@RequestParam("term") String term) {
		HttpStatus status = null;
		PageSearchDTO<WordCloudFilterDTO> res = new PageSearchDTO<WordCloudFilterDTO>();
		try {
			//Call Service
			Page<WordCloudFilter> result = service.searchWithPagination(term, 0);
			//Convert to DTO
			List<WordCloudFilterDTO> resultList = new ArrayList<WordCloudFilterDTO>();
			result.getContent().forEach(element -> {
				WordCloudFilterDTO tmp = new WordCloudFilterDTO();
				BeanUtils.copyProperties(element, tmp);
				resultList.add(tmp);
			});
			res.setMaxPage(result.getTotalPages());
			res.setObjList(resultList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<PageSearchDTO<WordCloudFilterDTO>>(res,status);
	}
	
	@GetMapping(value = "/getPage")
	public ResponseEntity<PageSearchDTO<WordCloudFilterDTO>> searchGetPage(@RequestParam("term") String term, @RequestParam("page") int page) {
		HttpStatus status = null;
		PageSearchDTO<WordCloudFilterDTO> res = new PageSearchDTO<WordCloudFilterDTO>();
		try {
			//Call Service
			Page<WordCloudFilter> result = service.searchWithPagination(term, page);
			//Convert to DTO
			List<WordCloudFilterDTO> resultList = new ArrayList<WordCloudFilterDTO>();
			result.getContent().forEach(element -> {
				WordCloudFilterDTO tmp = new WordCloudFilterDTO();
				BeanUtils.copyProperties(element, tmp);
				resultList.add(tmp);
			});
			res.setMaxPage(result.getTotalPages());
			res.setObjList(resultList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<PageSearchDTO<WordCloudFilterDTO>>(res,status);
	}
	
	@PostMapping
	public ResponseEntity<Integer> create(@RequestBody WordCloudFilterDTO dto) {
		HttpStatus status = null;
		try {
			WordCloudFilter entity = new WordCloudFilter();
			BeanUtils.copyProperties(dto, entity);
			service.addNewFilter(entity);
			status = HttpStatus.CREATED;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Integer>(status.value(), status);
	}
	
	@PutMapping
	public ResponseEntity<Integer> update(@RequestBody WordCloudFilterDTO dto) {
		HttpStatus status = null;
		try {
			WordCloudFilter entity = new WordCloudFilter();
			BeanUtils.copyProperties(dto, entity);
			service.modifyFilter(entity);
			status = HttpStatus.CREATED;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Integer>(status.value(), status);
	}
	
}
