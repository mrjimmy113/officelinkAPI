package com.fpt.officelink.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpt.officelink.dto.DepartmentDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Department;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.service.DepartmentService;

@RestController
@RequestMapping("/department")
public class DepartmentController {

	private CustomUser user;

	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Autowired
	DepartmentService depService;

	@GetMapping(value = "/getAll")
	public ResponseEntity<List<DepartmentDTO>> getAllByWorkplace() {
		this.user = getUserContext();
		HttpStatus status = null;
		List<DepartmentDTO> res = new ArrayList<DepartmentDTO>();

		try {
			//
			List<Department> result = depService.getAllByWorkplace(user.getWorkplaceId());
			//
			List<DepartmentDTO> resultList = new ArrayList<DepartmentDTO>();
			result.forEach(element -> {
				DepartmentDTO dto = new DepartmentDTO();
				BeanUtils.copyProperties(element, dto);
				resultList.add(dto);
			});
			//
			res.addAll(resultList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<List<DepartmentDTO>>(res, status);
	}


	@GetMapping
	public ResponseEntity<PageSearchDTO<DepartmentDTO>> searchGetPage(@RequestParam("term") String term,
			@RequestParam("page") int page) {
		this.user = getUserContext();
		HttpStatus status = null;
		PageSearchDTO<DepartmentDTO> res = new PageSearchDTO<DepartmentDTO>();

		try {
			//
			Page<Department> result = depService.searchWithPagination(term, user.getWorkplaceId(), page);
			//
			List<DepartmentDTO> resultList = new ArrayList<DepartmentDTO>();
			result.getContent().forEach(element -> {
				DepartmentDTO dto = new DepartmentDTO();
				BeanUtils.copyProperties(element, dto);
				resultList.add(dto);
			});
			//
			res.setMaxPage(result.getTotalPages());
			res.setObjList(resultList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<PageSearchDTO<DepartmentDTO>>(res, status);
	}

	@PostMapping
	public ResponseEntity<Integer> create(@RequestBody DepartmentDTO dto) {
		user = getUserContext();
		HttpStatus status = null;
		try {
			Department entity = new Department();
			Workplace wEntity = new Workplace(); 
			
			BeanUtils.copyProperties(dto, entity);
			wEntity.setId(user.getWorkplaceId());

			entity.setWorkplace(wEntity);
			boolean isSucceed = depService.addNewDepartment(entity);
			if (isSucceed) {
				status = HttpStatus.CREATED;
			} else {
				status = HttpStatus.CONFLICT;
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<Integer>(status.value(), status);
	}

	@PutMapping
	public ResponseEntity<Integer> update(@RequestBody DepartmentDTO dto) {
		this.user = getUserContext();
		HttpStatus status = null;
		try {
			Department entity = new Department();
			Workplace wEntity = new Workplace(); 
			
			BeanUtils.copyProperties(dto, entity);
			wEntity.setId(user.getWorkplaceId());
			
			entity.setWorkplace(wEntity);
			depService.modifyDepartment(entity);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<Integer>(status.value(), status);
	}

	@DeleteMapping
	public ResponseEntity<Integer> delete(@RequestParam("id") int id) {
		HttpStatus status = null;
		try {
			boolean success = depService.removeDepartment(id);
			
			if (success) {
				status = HttpStatus.OK;
			} else {
				status = HttpStatus.CONFLICT;
			}
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<Integer>(status.value(), status);
	}
	
	@GetMapping("/byLocation")
	public ResponseEntity<List<DepartmentDTO>> findByLocationId(@RequestParam("id") int id) {
		HttpStatus status = null;
		List<DepartmentDTO> res = new ArrayList<DepartmentDTO>();
		try {
			List<Department> result = depService.getByLocationId(id);
			result.forEach(element -> {
				DepartmentDTO dto = new DepartmentDTO();
				BeanUtils.copyProperties(element, dto);
				res.add(dto);
			});
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<List<DepartmentDTO>>(res,status);
	}
}
