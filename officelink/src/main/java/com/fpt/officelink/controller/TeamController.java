package com.fpt.officelink.controller;

import java.util.ArrayList;
import java.util.List;

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

import com.fpt.officelink.dto.AccountDTO;
import com.fpt.officelink.dto.DepartmentDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.dto.TeamDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Department;
import com.fpt.officelink.entity.Team;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.service.TeamService;

@RestController
@RequestMapping("/team")
public class TeamController {

	private CustomUser user;

	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Autowired
	TeamService teamService;

	@Secured({ "ROLE_employer", "ROLE_system_admin" })
	@GetMapping
	public ResponseEntity<PageSearchDTO<TeamDTO>> searchGetPage(@RequestParam("term") String term,
			@RequestParam("page") int page) {
		this.user = getUserContext();
		HttpStatus status = null;
		PageSearchDTO<TeamDTO> res = new PageSearchDTO<TeamDTO>();

		try {
			//
			Page<Team> result = teamService.searchWithPagination(term, user.getWorkplaceId(), page);
			//
			List<TeamDTO> resultList = new ArrayList<TeamDTO>();
			result.getContent().forEach(element -> {
				DepartmentDTO depDTO = new DepartmentDTO();
				TeamDTO teamDTO = new TeamDTO();
				BeanUtils.copyProperties(element.getDepartment(), depDTO);
				BeanUtils.copyProperties(element, teamDTO);

				List<AccountDTO> accounts = new ArrayList<AccountDTO>();
				for (Account acc : element.getAccounts()) {
					if (!acc.isDeleted()) {
						AccountDTO accDTO = new AccountDTO();
						BeanUtils.copyProperties(acc, accDTO);
						accounts.add(accDTO);
					}
				}

				teamDTO.setAccounts(accounts);
				teamDTO.setDepartment(depDTO);
				resultList.add(teamDTO);
			});

			res.setMaxPage(result.getTotalPages());
			res.setObjList(resultList);
			status = HttpStatus.OK;
		} catch (Exception e) {

			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<PageSearchDTO<TeamDTO>>(res, status);
	}

	@Secured({ "ROLE_employer", "ROLE_system_admin" })
	@GetMapping("getByWorkplace")
	public ResponseEntity<List<TeamDTO>> findByWorkplace() {
		this.user = getUserContext();
		HttpStatus status = null;
		List<TeamDTO> res = new ArrayList<TeamDTO>();
		try {
			List<Team> result = teamService.getTeamsByWorkplace(user.getWorkplaceId());
			result.forEach(element -> {
				TeamDTO teamDTO = new TeamDTO();
				BeanUtils.copyProperties(element, teamDTO);
				res.add(teamDTO);
			});
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<List<TeamDTO>>(res, status);
	}

	@Secured({ "ROLE_employer", "ROLE_system_admin" })
	@GetMapping("dep")
	public ResponseEntity<List<TeamDTO>> findByDepId(@RequestParam("id") Integer id) {
		HttpStatus status = null;
		List<TeamDTO> res = new ArrayList<TeamDTO>();
		try {
			List<Team> result = teamService.getTeamByDepartmentId(id);
			result.forEach(element -> {
				DepartmentDTO depDTO = new DepartmentDTO();
				TeamDTO teamDTO = new TeamDTO();
				BeanUtils.copyProperties(element.getDepartment(), depDTO);
				BeanUtils.copyProperties(element, teamDTO);
				teamDTO.setDepartment(depDTO);
				res.add(teamDTO);
			});
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<List<TeamDTO>>(res, status);
	}

	@Secured({ "ROLE_employer", "ROLE_system_admin" })
	@PostMapping
	public ResponseEntity<Integer> create(@RequestBody TeamDTO dto) {
		this.user = getUserContext();
		HttpStatus status = null;

		try {
			Team teamEntity = new Team();
			Department depEntity = new Department();
			Workplace wEntity = new Workplace();

			BeanUtils.copyProperties(dto.getDepartment(), depEntity);
			BeanUtils.copyProperties(dto, teamEntity);
			wEntity.setId(user.getWorkplaceId());

			depEntity.setWorkplace(wEntity);
			teamEntity.setDepartment(depEntity);
			boolean isSucceed = teamService.addNewTeam(teamEntity);
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

	@Secured({ "ROLE_employer", "ROLE_system_admin" })
	@PutMapping
	public ResponseEntity<Integer> update(@RequestBody TeamDTO dto) {
		this.user = getUserContext();
		HttpStatus status = null;

		try {
			Department depEntity = new Department();
			Team teamEntity = new Team();
			Workplace wEntity = new Workplace();

			BeanUtils.copyProperties(dto.getDepartment(), depEntity);
			BeanUtils.copyProperties(dto, teamEntity);
			wEntity.setId(user.getWorkplaceId());

			depEntity.setWorkplace(wEntity);
			teamEntity.setDepartment(depEntity);

			boolean isSucceed = teamService.modifyTeam(teamEntity);
			if (isSucceed) {
				status = HttpStatus.OK;
			} else {
				status = HttpStatus.CONFLICT;
			}
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<Integer>(status.value(), status);
	}

	@Secured({ "ROLE_employer", "ROLE_system_admin" })
	@DeleteMapping
	public ResponseEntity<Integer> delete(@RequestParam("id") int id) {
		this.user = getUserContext();
		HttpStatus status = null;

		try {
			boolean success = teamService.removeTeam(id, this.user.getWorkplaceId());

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
}
