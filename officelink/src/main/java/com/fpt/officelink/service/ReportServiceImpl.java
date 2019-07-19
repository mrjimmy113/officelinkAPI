package com.fpt.officelink.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.ServletContext;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fpt.officelink.dto.DashBoardDTO;
import com.fpt.officelink.dto.DepartmentDTO;
import com.fpt.officelink.dto.ImageNewsDTO;
import com.fpt.officelink.dto.LocationDTO;
import com.fpt.officelink.dto.SurveyReportDTO;
import com.fpt.officelink.dto.SurveySendDetailDTO;
import com.fpt.officelink.dto.TeamDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Department;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.entity.News;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveySendTarget;
import com.fpt.officelink.entity.Team;
import com.fpt.officelink.repository.AccountRespository;
import com.fpt.officelink.repository.DepartmentRepository;
import com.fpt.officelink.repository.LocationRepository;
import com.fpt.officelink.repository.NewsRepository;
import com.fpt.officelink.repository.SurveyRepository;
import com.fpt.officelink.repository.SurveySendTargetRepository;
import com.fpt.officelink.repository.TeamRepository;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	AccountRespository accRep;

	@Autowired
	SurveySendTargetRepository targetRep;

	@Autowired
	LocationRepository locationRep;

	@Autowired
	DepartmentRepository depRep;

	@Autowired
	TeamRepository teamRep;

	@Autowired
	SurveyRepository surRep;
	
	@Autowired
	NewsRepository newsRep;
	
	@Autowired
    ServletContext context;

	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Override
	public SurveySendDetailDTO getSendDetail(int surveyId) {
		SurveySendDetailDTO result = new SurveySendDetailDTO();
		Survey survey = surRep.findById(surveyId).get();
		List<SurveySendTarget> targets = null;
		if (survey.getTemplateId() != 0) {
			targets = targetRep.findAllBySurveyId(survey.getTemplateId());
		} else {
			targets = targetRep.findAllBySurveyId(surveyId);
		}
		Set<Location> locations = new HashSet<Location>();
		Set<Department> departments = new HashSet<Department>();
		Set<Team> teams = new HashSet<Team>();
		Optional<Account> currentLogin = accRep.findByEmail(getUserContext().getUsername());
		int workplaceId = getUserContext().getWorkplaceId();

		if (currentLogin.isPresent()) {
			Account currentAcc = currentLogin.get();
			boolean isAll = false;
			for (SurveySendTarget target : targets) {
				if (target.getDepartment() == null && targets.get(0).getLocation() == null
						&& targets.get(0).getTeam() == null) {
					isAll = true;
					break;
				}
			}
			
			if (!isAll) {
				for (SurveySendTarget target : targets) {
					if (target.getDepartment() != null && target.getLocation() == null && target.getTeam() == null) {
						departments.add(target.getDepartment());
						locations.addAll(locationRep.findAllByDepartmentId(target.getDepartment().getId()));
						teams.addAll(teamRep.findAllByDepartmentId(target.getDepartment().getId()));
					} else if (target.getDepartment() == null && target.getLocation() != null
							&& target.getTeam() == null) {
						locations.add(target.getLocation());
						departments.addAll(depRep.findAllByLocationId(target.getLocation().getId()));
						teams.addAll(teamRep.findAllByLocationId(target.getLocation().getId()));
					} else if (target.getDepartment() != null && target.getLocation() != null
							&& target.getTeam() == null) {
						locations.add(target.getLocation());
						departments.add(target.getDepartment());
						teams.addAll(teamRep.findAllByLocationIdAndDepartmentId(target.getLocation().getId(),
								target.getDepartment().getId()));
					}else if (target.getDepartment() != null && target.getLocation() != null
							&& target.getTeam() != null) {
						teams.add(target.getTeam());
					}
					
				}
			}

			switch (currentAcc.getRole().getRole()) {
			case "employer": {
				if (isAll) {
					locations.addAll(this.locationRep.findAllByWorkplaceIdAndIsDeleted(workplaceId, false));
					departments.addAll(this.depRep.findAllByWorkplaceIdAndIsDeleted(workplaceId, false));
					teams.addAll(this.teamRep.findAllByWorkplaceId(workplaceId, false));
				}
				break;
			}
			case "employee": {
				if (isAll) {
					for (Team team : currentAcc.getTeams()) {
						departments.add(team.getDepartment());
					}
					teams.addAll(currentAcc.getTeams());
				} else {
					locations = new HashSet<Location>();
					Set<Department> tmpDeps = new HashSet<Department>();
					Set<Team> tmpTeams = new HashSet<Team>();
					for (Team team : currentAcc.getTeams()) {
						if (departments.contains(team.getDepartment())) {
							tmpDeps.add(team.getDepartment());
						}
						if (teams.contains(team)) {
							tmpTeams.add(team);
						}
					}
					departments = tmpDeps;
					teams = tmpTeams;
				}
				break;
			}
			case "manager": {
				if (isAll) {
					locations.add(currentAcc.getLocation());
					for (Team team : currentAcc.getTeams()) {
						departments.add(team.getDepartment());
					}
					teams.addAll(currentAcc.getTeams());
				} else {
					Set<Location> tmpLocations = new HashSet<Location>();
					Set<Department> tmpDeps = new HashSet<Department>();
					Set<Team> tmpTeams = new HashSet<Team>();
					if (locations.contains(currentAcc.getLocation())) {
						tmpLocations.add(currentAcc.getLocation());
					}
					for (Team team : currentAcc.getTeams()) {
						if (departments.contains(team.getDepartment())) {
							tmpDeps.add(team.getDepartment());
						}
						if (teams.contains(team)) {
							tmpTeams.add(team);
						}
					}
					locations = tmpLocations;
					departments = tmpDeps;
					teams = tmpTeams;
				}
				break;
			}

			}
			List<LocationDTO> locationDTOs = new ArrayList<LocationDTO>();
			List<DepartmentDTO> departmentDTOs = new ArrayList<DepartmentDTO>();
			List<TeamDTO> teamDTOs = new ArrayList<TeamDTO>();
			locations.forEach(e -> {
				LocationDTO dto = new LocationDTO();
				BeanUtils.copyProperties(e, dto);
				locationDTOs.add(dto);
			});
			departments.forEach(e -> {
				DepartmentDTO dto = new DepartmentDTO();
				BeanUtils.copyProperties(e, dto);
				departmentDTOs.add(dto);
			});
			teams.forEach(e -> {
				TeamDTO dto = new TeamDTO();
				BeanUtils.copyProperties(e, dto);
				teamDTOs.add(dto);
			});
			result.setLocations(locationDTOs);
			result.setDepartments(departmentDTOs);
			result.setTeams(teamDTOs);
		}
		return result;
	}

	@Override
	public DashBoardDTO getDashBoard(Integer id) throws IOException {
		DashBoardDTO result = new DashBoardDTO();
		PageRequest top5 = PageRequest.of(0, 5);
		PageRequest top1 = PageRequest.of(0, 1);
		result.setAccount(accRep.countByWorkplaceId(id));
		result.setTeam(teamRep.countByWorkplaceId(id));
		result.setDepartment(depRep.countDepartmentOnWorkplace(id));
		List<LocationDTO> res = new ArrayList<>();
		List<Location> resultLocation = locationRep.findAllByWorkplaceIdAndIsDeleted(id, false);
		resultLocation.forEach(element -> {
			LocationDTO dto = new LocationDTO();
			BeanUtils.copyProperties(element, dto);
			res.add(dto);
		});
		result.setLocation(res);
		
		List<News> newsList = newsRep.findLastestNews(id,top1).getContent();
		System.out.print(newsList.size());
		if(!newsList.isEmpty()) {
			ImageNewsDTO dto = new ImageNewsDTO();
	        BeanUtils.copyProperties(newsList.get(0), dto);
	        String tmp = context.getRealPath("") + "image\\" + dto.getImage();
	        Path byteImage = Paths.get(tmp);
	        byte[] data = Files.readAllBytes(byteImage);
	        dto.setByte_image(data);
	        result.setNews(dto);
		}
        List<Survey> surveys = surRep.findTop5LastestSendOutSurvey(id,top5).getContent();
        List<SurveyReportDTO> dtoList = new ArrayList<SurveyReportDTO>();
        for (Survey sur : surveys) {
        	SurveyReportDTO dtoReport = new SurveyReportDTO();
			BeanUtils.copyProperties(sur, dtoReport);
			dtoList.add(dtoReport);
		}
        result.setReports(dtoList);
     
		return result;
	}
}
