package com.fpt.officelink.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Answer;
import com.fpt.officelink.entity.AnswerReport;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.entity.SurveySendTarget;
import com.fpt.officelink.entity.Team;
import com.fpt.officelink.entity.TeamQuestionReport;
import com.fpt.officelink.entity.WordCloud;
import com.fpt.officelink.repository.AnswerReportRepository;
import com.fpt.officelink.repository.AnswerRepository;
import com.fpt.officelink.repository.SurveyQuestionRepository;
import com.fpt.officelink.repository.SurveyRepository;
import com.fpt.officelink.repository.SurveySendTargetRepository;
import com.fpt.officelink.repository.TeamQuestionReportRepository;
import com.fpt.officelink.repository.TeamRepository;

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

	/**
	  Generate a TeamQuestionReport entity for a survey
	 */
	@Async
	@Override
	@Transactional
	public void generateTeamQuestionReport(int surveyId) {
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
				List<Answer> answers = answerRep.findAllByIndentityAndTeamId(sQuestion.getId(), team.getId());
				// get team's answer report
				switch (sQuestion.getQuestion().getType().getType()) {
				case MULTIPLE:
					arList = generateMutipleChoiceReport(answers);
					break;
				case SINGLE:
					arList = generateSingleChoiceReport(answers);
					break;
				case TEXT:
					arList = generateFreeTextReport(answers);
					break;
				}
				
				// Save report
				questionReport.addAnswerReport(arList);
				teamQuestionReportRep.save(questionReport);
			}
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

		if (survey.getTemplateId() != 0) {
			targets = targetRep.findAllBySurveyId(survey.getTemplateId());
		} else {
			targets = targetRep.findAllBySurveyId(surveyId);
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

	
	private List<AnswerReport> generateFreeTextReport(List<Answer> answers) {
		List<AnswerReport> result = new ArrayList<AnswerReport>();
		boolean isFound;
		for (Answer a : answers) {
			for (WordCloud w : a.getWordClouds()) {
				isFound = false;
				for (AnswerReport r : result) {
					if (r.getTerm().equalsIgnoreCase(w.getWord())) {
						r.setWeight(r.getWeight() + w.getTimes());
						isFound = true;
						break;
					}
				}
				if (!isFound) {
					result.add(new AnswerReport(w.getWord(), w.getTimes()));
				}

			}

		}
		return result;
	}

	// Report: Single choice
	private List<AnswerReport> generateSingleChoiceReport(List<Answer> answers) {
		List<AnswerReport> result = new ArrayList<AnswerReport>();
		boolean isFound;
		for (Answer answer : answers) {
			isFound = false;
			for (AnswerReport ansReport : result) {
				if (answer.getContent().equalsIgnoreCase(ansReport.getTerm())) {
					ansReport.setWeight(ansReport.getWeight() + 1);
					isFound = true;
					break;
				}
			}
			if (!isFound)
				result.add(new AnswerReport(answer.getContent(), 1));
		}
		return result;
	}

	
	// Report: Mutiple Choice
	private List<AnswerReport> generateMutipleChoiceReport(List<Answer> answers) {
		List<AnswerReport> result = new ArrayList<AnswerReport>();
		boolean isFound;
		for (Answer answer : answers) {
			String[] options = answer.getContent().split(",");
			for (int i = 0; i < options.length; i++) {
				isFound = false;
				for (AnswerReport AnswerReport : result) {
					if (options[i].equalsIgnoreCase(AnswerReport.getTerm())) {
						AnswerReport.setWeight(AnswerReport.getWeight() + 1);
						isFound = true;
						break;
					}
				}
				if (!isFound)
					result.add(new AnswerReport(options[i], 1));
			}
		}
		return result;
	}
}
