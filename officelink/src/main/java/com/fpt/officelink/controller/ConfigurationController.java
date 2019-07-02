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

import com.fpt.officelink.dto.ConfigurationDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.entity.Configuration;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.service.ConfigurationService;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {

	@Autowired
	ConfigurationService configService;

	@GetMapping(value = "/getConfig")
	public ResponseEntity<ConfigurationDTO> getConfig(@RequestParam("id") Integer id) {
		HttpStatus status = null;
		ConfigurationDTO res = new ConfigurationDTO();

		try {
			//
			Configuration result = configService.getConfigById(id);
			//
			BeanUtils.copyProperties(result, res);
			//
			res.setWorkplaceId(result.getWorkplace().getId());
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<ConfigurationDTO>(res, status);
	}

	/*
	 * 
	 * */
	@GetMapping(value = "/workplaceConfigs")
	public ResponseEntity<PageSearchDTO<ConfigurationDTO>> getAllByWorkplace(
			@RequestParam("workplaceId") Integer workplaceId, @RequestParam("page") int page) {
		HttpStatus status = null;
		PageSearchDTO<ConfigurationDTO> res = new PageSearchDTO<ConfigurationDTO>();

		try {
			//
			Page<Configuration> result = configService.getWithPagination(workplaceId, page);
			//
			List<ConfigurationDTO> resultList = new ArrayList<ConfigurationDTO>();
			result.forEach(element -> {
				ConfigurationDTO dto = new ConfigurationDTO();

				BeanUtils.copyProperties(element, dto);

				dto.setWorkplaceId(element.getWorkplace().getId());
				resultList.add(dto);
			});
			//
			res.setMaxPage(result.getTotalPages());
			res.setObjList(resultList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<PageSearchDTO<ConfigurationDTO>>(res, status);
	}

	@PostMapping
	public ResponseEntity<Integer> create(@RequestBody ConfigurationDTO dto) {
		HttpStatus status = null;
		try {
			Configuration entity = new Configuration();
			Workplace wEntity = new Workplace();
			BeanUtils.copyProperties(dto, entity);
			
			wEntity.setId(dto.getWorkplaceId());
			entity.setWorkplace(wEntity);

			boolean isSucceed = configService.addNewConfig(entity);
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
	public ResponseEntity<Integer> update(@RequestBody ConfigurationDTO dto) {
		HttpStatus status = null;
		try {
			Configuration entity = new Configuration();
			Workplace wEntity = new Workplace();
			BeanUtils.copyProperties(dto, entity);
			
			wEntity.setId(dto.getWorkplaceId());
			entity.setWorkplace(wEntity);
			
			configService.modifyConfig(entity);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
			e.printStackTrace();
		}

		return new ResponseEntity<Integer>(status.value(), status);
	}

	@DeleteMapping
	public ResponseEntity<Integer> delete(@RequestParam("id") int id) {
		HttpStatus status = null;
		try {
			configService.removeConfig(id);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<Integer>(status.value(), status);
	}
}
