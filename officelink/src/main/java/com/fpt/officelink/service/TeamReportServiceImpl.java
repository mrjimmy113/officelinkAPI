package com.fpt.officelink.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.fpt.officelink.repository.SurveyQuestionRepository;
import com.fpt.officelink.repository.SurveyRepository;
import com.fpt.officelink.repository.SurveySendTargetRepository;
import com.fpt.officelink.repository.TeamQuestionReportRepository;
import com.fpt.officelink.repository.TeamRepository;

@Service
public class TeamReportServiceImpl implements TeamReportService {

	@Autowired
	SurveyRepository surRep;
	
	@Autowired
	SurveySendTargetRepository targetRep;
	
	@Autowired
	SurveyQuestionRepository surQuestRep;
	
	@Autowired
	TeamQuestionReportRepository teamQuestionReportRep;
	
	@Autowired
	AnswerReportRepository answerReportRep;
	
	@Autowired
	TeamRepository teamRep;
	
	@Async
	@Override
	public void generateTeamQuestionReport(int surveyId) {
		// get list of teams that this survey was send to
		Survey survey = surRep.findById(surveyId).get();
		List<SurveySendTarget> targets = null;
		if (survey.getTemplateId() != 0) {
			targets = targetRep.findAllBySurveyId(survey.getTemplateId());
		} else {
			targets = targetRep.findAllBySurveyId(surveyId);
		}
		
		Set<Team> teams = new HashSet<Team>();
		boolean isAll = false;
		if (targets.get(0).getDepartment() == null && targets.get(0).getLocation() == null
				&& targets.get(0).getTeam() == null) {
			isAll = true;
		}
		
		if (isAll) {
			teams.addAll(teamRep.findAllByWorkplaceId(survey.getWorkplace().getId(), false));
		} else {
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

		// Get survey question of each team
		List<SurveyQuestion> surveyQuestions = surQuestRep.findAllBySurveyId(surveyId);
		for (Team team : teams) {
			TeamQuestionReport questionReport = new TeamQuestionReport();

			// set team for report
			for (SurveyQuestion sQuestion : surveyQuestions) {
				questionReport.setTeam(team);
				questionReport.setSurveyQuestion(sQuestion);
				
				// get answer report
				List<AnswerReport> arList = new ArrayList<AnswerReport>();
				switch (sQuestion.getQuestion().getType().getType()) {
				case MULTIPLE:
					arList = generateMutipleChoiceReport(new ArrayList<Answer>(sQuestion.getAnswers()));
					break;
				case SINGLE:
					arList = generateSingleChoiceReport(new ArrayList<Answer>(sQuestion.getAnswers()));
					break;
				case TEXT:
					arList = generateFreeTextReport(new ArrayList<Answer>(sQuestion.getAnswers()));
					break;
				}
				
				teamQuestionReportRep.save(questionReport);
				
				for (AnswerReport answerReport : arList) {
					answerReport.setQuestionReport(questionReport);
				}
				
				questionReport.setAnswerReports(arList);
				teamQuestionReportRep.save(questionReport);
			}
		}
	}
	
	// Report: Free Text
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
