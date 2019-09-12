package com.fpt.officelink.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.AnswerReport;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.entity.SurveySendTarget;
import com.fpt.officelink.entity.Team;
import com.fpt.officelink.entity.TeamQuestionReport;
import com.fpt.officelink.repository.AnswerReportRepository;
import com.fpt.officelink.repository.AnswerRepository;
import com.fpt.officelink.repository.MultipleAnswerRepository;
import com.fpt.officelink.repository.SingleChoiceRepository;
import com.fpt.officelink.repository.SurveyQuestionRepository;
import com.fpt.officelink.repository.SurveyRepository;
import com.fpt.officelink.repository.SurveySendTargetRepository;
import com.fpt.officelink.repository.TeamQuestionReportRepository;
import com.fpt.officelink.repository.TeamRepository;
import com.fpt.officelink.repository.WordCloudRepository;

/**
 * @author phduo
 *
 */
@Service
public class TeamReportServiceImpl implements TeamReportService {

	@Autowired
	SurveyRepository surRep;

	@Autowired
	SurveySendTargetRepository targetRep;

	@Autowired
	SurveyQuestionRepository surQuestRep;

	@Autowired
	AnswerRepository answerRep;

	@Autowired
	TeamQuestionReportRepository teamQuestionReportRep;

	@Autowired
	AnswerReportRepository answerReportRep;

	@Autowired
	TeamRepository teamRep;
	
	@Autowired
	SingleChoiceRepository singleRep;
	
	@Autowired
	MultipleAnswerRepository multiRep;
	
	@Autowired
	WordCloudRepository textRep;

	/**
	  Generate a TeamQuestionReport entity for a survey
	 */
	@Async
	@Override
	@Transactional
	public Future<Boolean> generateTeamQuestionReport(int surveyId) {
		AsyncResult<Boolean> isSuccess = new AsyncResult<Boolean>(false);
		
		try {
			// get all the team the survey was sent to
			Set<Team> teams = this.getTeamsSent(surveyId);
			
			// Get the list of survey-questions
			List<SurveyQuestion> surveyQuestions = surQuestRep.findAllBySurveyId(surveyId);
			
			for (Team team : teams) {
				for (SurveyQuestion sQuestion : surveyQuestions) {
					// init a team question report
					TeamQuestionReport questionReport = new TeamQuestionReport();
					questionReport.setTeam(team);
					questionReport.setSurveyQuestion(sQuestion);

					List<AnswerReport> arList = new ArrayList<AnswerReport>();
					// get team's answer of survey question
					
					// get team's answer report
//					switch (sQuestion.getQuestion().getType().getType()) {
//					case MULTIPLE:
//						arList = multiRep.findAllByIndentityAndTeamId(sQuestion.getId(), team.getId());
//						break;
//					case SINGLE:
//						arList = singleRep.findAllByIndentityAndTeamId(sQuestion.getId(), team.getId());
//						break;
//					case TEXT:
//						arList = textRep.findAllByIndentityAndTeamId(sQuestion.getId(), team.getId());
//						break;
//					}
					
					// Save report
//					questionReport.addAnswerReport(arList);
					teamQuestionReportRep.save(questionReport);
				}
			}
			
			isSuccess = new AsyncResult<Boolean>(true);
			return isSuccess;
		} catch (Exception e) {
			e.printStackTrace();
			return isSuccess;
		}
		
	}

	/**
	 * Get list of teams that a survey was sent to
	 * @param surveyId
	 * @return list of teams
	 */
	private Set<Team> getTeamsSent(int surveyId) {
		Set<Team> teams = new HashSet<Team>();
		
		// find the survey
		Survey survey = surRep.findById(surveyId).get();

		// Find the survey target
		List<SurveySendTarget> targets = null;

		if (survey.isTemplate()) {
			targets = targetRep.findAllBySurveyIdAndIsNeed(survey.getTemplateId(),true);
		} else {
			targets = targetRep.findAllBySurveyIdAndIsNeed(surveyId,true);
		}

		// if the survey was sent to the whole company
		boolean isAll = false;
		if (targets.get(0).getDepartment() == null && targets.get(0).getLocation() == null
				&& targets.get(0).getTeam() == null) {
			isAll = true;
		}

		// get all team in company if the above flag is true
		if (isAll) {
			teams.addAll(teamRep.findAllByWorkplaceId(survey.getWorkplace().getId(), false));
		} else { // get all respective teams if isAll flag is false
			for (SurveySendTarget target : targets) {
				// department is present
				if (target.getDepartment() != null && target.getLocation() == null && target.getTeam() == null) {
					teams.addAll(teamRep.findAllByDepartmentId(target.getDepartment().getId()));
					continue;
				}
				// location is present
				else if (target.getDepartment() == null && target.getLocation() != null && target.getTeam() == null) {
					teams.addAll(teamRep.findAllByLocationId(target.getLocation().getId()));
					continue;
				}
				// department and location is present
				else if (target.getDepartment() != null && target.getLocation() != null && target.getTeam() == null) {
					teams.addAll(teamRep.findAllByLocationIdAndDepartmentId(target.getLocation().getId(),
							target.getDepartment().getId()));
					continue;
				}
				// team is present
				else if (target.getDepartment() == null && target.getLocation() == null && target.getTeam() != null) {
					teams.add(target.getTeam());
					continue;
				}
			}
		}
		
		return teams;
	}

	
	
}
