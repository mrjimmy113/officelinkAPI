package com.fpt.officelink.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.fpt.officelink.dto.TeamDTO;
import com.fpt.officelink.entity.Department;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.service.DepartmentService;

@RestController
@RequestMapping("/department")
public class DepartmentController {
	
	@Autowired
	DepartmentService depService;
	
	
	@GetMapping(value = "/getDep")
	public ResponseEntity<DepartmentDTO> getDep(@RequestParam("depId") Integer depId){
		HttpStatus status = null;
		DepartmentDTO res = new DepartmentDTO();
		
		try {
			//
			Department result = depService.getDepartment(depId);
			List<TeamDTO> listTeamDTO = new ArrayList<TeamDTO>();
			//
			BeanUtils.copyProperties(result, res);
			
			result.getTeams().forEach(t -> {
				TeamDTO teamDTO = new TeamDTO();
				BeanUtils.copyProperties(t, teamDTO);
				listTeamDTO.add(teamDTO);
			});
			
			//
			res.setTeams(listTeamDTO);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<DepartmentDTO>(res, status);
	}

	@GetMapping(value = "/getAll")
	public ResponseEntity<List<DepartmentDTO>> getAll(){
		HttpStatus status = null;
		List<DepartmentDTO> res = new ArrayList<DepartmentDTO>();
		
		try {
			//
			List<Department> result = depService.getAll();
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
	public ResponseEntity<PageSearchDTO<DepartmentDTO>> search(@RequestParam("term") String term){
		HttpStatus status = null;
		PageSearchDTO<DepartmentDTO> res = new PageSearchDTO<DepartmentDTO>();
		
		try {
			//
			Page<Department> result = depService.searchWithPagination(term, 0);
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
	
	@GetMapping(value = "/getPage")
	public ResponseEntity<PageSearchDTO<DepartmentDTO>> searchGetPage(@RequestParam("term") String term, @RequestParam("page") int page){
		HttpStatus status = null;
		PageSearchDTO<DepartmentDTO> res = new PageSearchDTO<DepartmentDTO>();
		
		try {
			//
			Page<Department> result = depService.searchWithPagination(term, page);
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
	public ResponseEntity<Integer> create(@RequestBody DepartmentDTO dto){
		HttpStatus status = null;
		try {
			Department entity = new Department();
//			List<Location> locations = new ArrayList<Location>();
			BeanUtils.copyProperties(dto, entity);
			
//			dto.getLocations().forEach(element -> {
//				Location location = new Location();
//				BeanUtils.copyProperties(element, location);
//				locations.add(location);
//			});
//			entity.setLocations(locations);
			
			boolean isSucceed = depService.addNewDepartment(entity);
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
	
	@PutMapping
	public ResponseEntity<Integer> update(@RequestBody DepartmentDTO dto){
		HttpStatus status = null;
		try {
			Department entity = new Department();
//			List<Location> locations = new ArrayList<Location>();
			BeanUtils.copyProperties(dto, entity);
			
//			dto.getLocations().forEach(element -> {
//				Location location = new Location();
//				BeanUtils.copyProperties(element, location);
//				locations.add(location);
//			});
//			entity.setLocations(locations);
			
			depService.modifyDepartment(entity);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<Integer>(status.value(), status);
	}
	
	@DeleteMapping
	public ResponseEntity<Integer> delete(@RequestParam("id") int id){
		HttpStatus status = null;
		try {
			depService.removeDepartment(id);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<Integer>(status.value(), status);
	}
}
