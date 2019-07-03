package com.fpt.officelink.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

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

import com.fpt.officelink.dto.ConfigurationDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.entity.Configuration;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.service.ConfigurationService;
import com.fpt.officelink.service.WorkplaceService;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {

	private CustomUser user;

	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Autowired
	ConfigurationService configService;

	@Autowired
	WorkplaceService workplaceService;

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
	@GetMapping(value = "/searchGetPage")
	public ResponseEntity<PageSearchDTO<ConfigurationDTO>> searchGetPage(@RequestParam("term") String term, @RequestParam("page") int page) {
		this.user = this.getUserContext();
		HttpStatus status = null;
		PageSearchDTO<ConfigurationDTO> res = new PageSearchDTO<ConfigurationDTO>();

		try {
			//
			Page<Configuration> result = configService.searchWithPagination(user.getWorkplaceId(), term, page);
			//
			List<ConfigurationDTO> resultList = new ArrayList<ConfigurationDTO>();
			result.forEach(element -> {
				ConfigurationDTO dto = new ConfigurationDTO();
				SurveyDTO surveyDto = new SurveyDTO();

				BeanUtils.copyProperties(element, dto);
				if (element.getSurvey() != null) {
					BeanUtils.copyProperties(element.getSurvey(), surveyDto);
				}

				dto.setSurvey(surveyDto);
				dto.setWorkplaceId(element.getWorkplace().getId());
				resultList.add(dto);
			});
			//
			res.setMaxPage(result.getTotalPages());
			res.setObjList(resultList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<PageSearchDTO<ConfigurationDTO>>(res, status);
	}

	@PostMapping
	public ResponseEntity<Integer> create(@Valid @RequestBody ConfigurationDTO dto) {
		this.user = this.getUserContext();
		HttpStatus status = null;

		try {
			Configuration entity = new Configuration();
			Workplace wEntity = new Workplace();
			Survey sEntity = new Survey();

			BeanUtils.copyProperties(dto, entity);
			if (dto.getSurvey() != null) {
				BeanUtils.copyProperties(dto.getSurvey(), sEntity);
			}

			wEntity.setId(user.getWorkplaceId());
			entity.setWorkplace(wEntity);
			entity.setSurvey(sEntity);

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
	public ResponseEntity<Integer> update(@Valid @RequestBody ConfigurationDTO dto) {
		HttpStatus status = null;
		try {
			Configuration entity = new Configuration();
			Workplace wEntity = new Workplace();
			Survey sEntity = new Survey();

			BeanUtils.copyProperties(dto, entity);
			if (dto.getSurvey() != null) {
				BeanUtils.copyProperties(dto.getSurvey(), sEntity);
			}

			wEntity.setId(dto.getWorkplaceId());
			entity.setWorkplace(wEntity);
			entity.setSurvey(sEntity);

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
