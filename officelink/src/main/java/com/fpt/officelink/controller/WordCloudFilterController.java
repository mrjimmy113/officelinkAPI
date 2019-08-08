package com.fpt.officelink.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.dto.WordCloudFilterDTO;
import com.fpt.officelink.dto.WordDTO;
import com.fpt.officelink.entity.Word;
import com.fpt.officelink.entity.WordCloudFilter;
import com.fpt.officelink.service.WordCloudFilterService;

@RestController
@RequestMapping("/wordCloud")
public class WordCloudFilterController {

	@Autowired
	WordCloudFilterService service;
	
	@Secured({"ROLE_employer","ROLE_system_admin"})
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
				List<WordDTO> tmpList = new ArrayList<WordDTO>();
				element.getWordList().forEach(e -> {
					WordDTO tmpW = new WordDTO();
					BeanUtils.copyProperties(e, tmpW,"filter");
					tmpList.add(tmpW);
				});
				BeanUtils.copyProperties(element, tmp);
				tmp.setWordList(tmpList);
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
	
	@Secured({"ROLE_employer","ROLE_system_admin"})
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
				List<WordDTO> tmpList = new ArrayList<WordDTO>();
				element.getWordList().forEach(e -> {
					WordDTO tmpW = new WordDTO();
					BeanUtils.copyProperties(e, tmpW,"filter");
					tmpList.add(tmpW);
				});
				BeanUtils.copyProperties(element, tmp);
				tmp.setWordList(tmpList);
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

	@Secured({"ROLE_employer","ROLE_system_admin"})
	@PostMapping
	public ResponseEntity<Integer> create(@RequestBody WordCloudFilterDTO dto) {
		HttpStatus status = null;
		Integer res = null;
		try {
			WordCloudFilter entity = new WordCloudFilter();
			List<Word> wordList = new ArrayList<Word>();
			BeanUtils.copyProperties(dto, entity);
			dto.getWordList().forEach(element -> {
				Word tmp = new Word();
				BeanUtils.copyProperties(element,tmp,"filter");
				wordList.add(tmp);
			});
			res = service.addNewFilter(entity, wordList);
			status = HttpStatus.CREATED;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Integer>(res, status);
	}
	
	@Secured({"ROLE_employer","ROLE_system_admin"})
	@PutMapping
	public ResponseEntity<Integer> update(@RequestBody WordCloudFilterDTO dto) {
		HttpStatus status = null;
		try {
			WordCloudFilter entity = new WordCloudFilter();
			List<Word> wordList = new ArrayList<Word>();
			BeanUtils.copyProperties(dto, entity);
			
			dto.getWordList().forEach(element -> {
				Word tmp = new Word();
				BeanUtils.copyProperties(element,tmp,"filter");
				wordList.add(tmp);
			});
			service.modifyFilter(entity,wordList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Integer>(status.value(), status);
	}
	
	@Secured({"ROLE_employer","ROLE_system_admin"})
	@DeleteMapping
	public ResponseEntity<Integer> delete(@RequestParam("id") Integer id) {
		HttpStatus status = null;
		try {
			service.delete(id);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<Integer>(status.value(),status);
	}
	
	@Secured({"ROLE_employer","ROLE_system_admin"})
	@GetMapping("/existed")
	public ResponseEntity<Boolean> checkIfSurveyExisted(@RequestParam("name") String name) {
		HttpStatus status = null;
		Boolean res = null;
		try {
			res = service.isExisted(name);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Boolean>(res,status);
	}
	
	@Secured({"ROLE_employer","ROLE_employee","ROLE_system_admin"})
	@GetMapping("/all")
	public ResponseEntity<List<WordCloudFilterDTO>> getAll() {
		HttpStatus status = null;
		List<WordCloudFilterDTO> res = new ArrayList<WordCloudFilterDTO>();
		try {
			List<WordCloudFilter>result = service.getAll();
			result.forEach(element -> {
				WordCloudFilterDTO tmp = new WordCloudFilterDTO();
				BeanUtils.copyProperties(element, tmp);
				res.add(tmp);
			});
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<List<WordCloudFilterDTO>>(res, status);
	}
	
	@Secured({"ROLE_employer","ROLE_employee","ROLE_system_admin"})
	@GetMapping("/one")
	public ResponseEntity<WordCloudFilterDTO> getOne(@RequestParam("id") Integer id) {
		HttpStatus status = null;
		WordCloudFilterDTO res = new WordCloudFilterDTO();
		try {
			WordCloudFilter result = service.getOneFilter(id);
			BeanUtils.copyProperties(result, res);
			List<WordDTO> tmpList = new ArrayList<WordDTO>();
			result.getWordList().forEach(e ->{
				WordDTO tmpW = new WordDTO();
				BeanUtils.copyProperties(e, tmpW,"filter");
				tmpList.add(tmpW);
			});
			res.setWordList(tmpList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<WordCloudFilterDTO>(res,status);
	}
}
