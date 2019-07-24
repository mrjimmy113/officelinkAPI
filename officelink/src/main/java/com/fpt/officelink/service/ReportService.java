package com.fpt.officelink.service;

import java.io.IOException;
import java.util.Optional;

import com.fpt.officelink.dto.DashBoardDTO;
import com.fpt.officelink.dto.SurveySendDetailDTO;
import com.fpt.officelink.entity.SurveyQuestion;

public interface ReportService {

	SurveySendDetailDTO getSendDetail(int surveyId);

	DashBoardDTO getDashBoard(Integer id) throws IOException;

	Optional<SurveyQuestion> getDownloadDetail(Integer surveyId, Integer questionId);

}
