package com.fpt.officelink.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.dto.WorkplaceDTO;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.service.WorkplaceService;

@RestController
@RequestMapping("/workplace")
public class WorkplaceController {
	
	private CustomUser user;

	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Autowired
	WorkplaceService workpService;

	@Secured({"ROLE_system_admin"})
	@GetMapping(value = "/getUserWorkplace")
	public ResponseEntity<WorkplaceDTO> getUserWorkplace(){
		user = this.getUserContext();
		HttpStatus status = null;
		WorkplaceDTO res = new WorkplaceDTO();
		
		try {
			//
			Workplace result = workpService.getWorkplace(user.getWorkplaceId());
			//
			BeanUtils.copyProperties(result, res);
			//
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<WorkplaceDTO>(res, status);
	}
	
	@Secured({"ROLE_system_admin"})
	@GetMapping
	public ResponseEntity<PageSearchDTO<WorkplaceDTO>> searchGetPage(@RequestParam("term") String term, @RequestParam("page") int page){
		HttpStatus status = null;
		PageSearchDTO<WorkplaceDTO> res = new PageSearchDTO<WorkplaceDTO>();
		
		try {
			//
			Page<Workplace> result = workpService.searchWithPagination(term, page);
			//
			List<WorkplaceDTO> resultList = new ArrayList<WorkplaceDTO>();
			result.getContent().forEach(element -> {
				WorkplaceDTO dto = new WorkplaceDTO();
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
		
		return new ResponseEntity<PageSearchDTO<WorkplaceDTO>>(res, status); 
	}
	
	@Secured({"ROLE_system_admin"})
	@PostMapping
	public ResponseEntity<Integer> create(@RequestBody WorkplaceDTO dto){
		HttpStatus status = null;
		try {
			Workplace entity = new Workplace();
			BeanUtils.copyProperties(dto, entity);
			
			boolean isSucceed = workpService.addNewWorkplace(entity);
			if (isSucceed) {
				status = HttpStatus.CREATED;				
			} else {
				status = HttpStatus.CONFLICT;
			}
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<Integer>(status.value(), status);
	}
	
	@Secured({"ROLE_system_admin"})
	@PutMapping
	public ResponseEntity<Integer> update(	@RequestBody WorkplaceDTO dto){
		HttpStatus status = null;
		try {
			Workplace entity = new Workplace();
			BeanUtils.copyProperties(dto, entity);
			
			workpService.modifyWorkplace(entity);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<Integer>(status.value(), status);
	}
	
	@Secured({"ROLE_system_admin"})
	@DeleteMapping
	public ResponseEntity<Integer> delete(@RequestParam("id") int id){
		HttpStatus status = null;
		try {
			workpService.removeWorkplace(id);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<Integer>(status.value(), status);
	}
}
