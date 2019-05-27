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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.dto.TeamDTO;
import com.fpt.officelink.entity.Team;
import com.fpt.officelink.service.TeamService;

@RestController
@RequestMapping("/team")
public class TeamController {
	
	@Autowired
	TeamService teamService;
	
	@GetMapping
	public ResponseEntity<PageSearchDTO<TeamDTO>> search(@RequestParam("term") String term){
		HttpStatus status = null;
		PageSearchDTO<TeamDTO> res = new PageSearchDTO<TeamDTO>();
		
		try {
			//
			Page<Team> result = teamService.searchWithPagination(term, 0);
			//
			List<TeamDTO> resultList = new ArrayList<TeamDTO>();
			result.getContent().forEach(element -> {
				TeamDTO temp = new TeamDTO();
				BeanUtils.copyProperties(element, temp);
				resultList.add(temp);
			});
			
			res.setMaxPage(result.getTotalPages());
			res.setObjList(resultList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<PageSearchDTO<TeamDTO>>(res, status);
	}
	
	@GetMapping
	public ResponseEntity<PageSearchDTO<TeamDTO>> searchGetPage(@RequestParam("term") String term, @RequestParam("page") int page){
		HttpStatus status = null;
		PageSearchDTO<TeamDTO> res = new PageSearchDTO<TeamDTO>();
		
		try {
			//
			Page<Team> result = teamService.searchWithPagination(term, page);
			//
			List<TeamDTO> resultList = new ArrayList<TeamDTO>();
			result.getContent().forEach(element -> {
				TeamDTO temp = new TeamDTO();
				BeanUtils.copyProperties(element, temp);
				resultList.add(temp);
			});
			
			res.setMaxPage(result.getTotalPages());
			res.setObjList(resultList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<PageSearchDTO<TeamDTO>>(res, status);
	}
	
	@PostMapping
	public ResponseEntity<Integer> create(@RequestBody TeamDTO dto){
		HttpStatus status = null;
		
		try {
			Team entity = new Team();
			BeanUtils.copyProperties(dto, entity);
			teamService.addNewTeam(entity);
			status = HttpStatus.CREATED;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<Integer>(status.value(), status);
	}
	
	@PostMapping
	public ResponseEntity<Integer> update(@RequestBody TeamDTO dto){
		HttpStatus status = null;
		
		try {
			Team entity = new Team();
			BeanUtils.copyProperties(dto, entity);
			teamService.modifyTeam(entity);
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
			teamService.removeTeam(id);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Integer>(status.value(), status);
	}
}
