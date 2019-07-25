package com.fpt.officelink.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

import com.fpt.officelink.dto.DashBoardDTO;
import com.fpt.officelink.dto.SurveySendDetailDTO;
import com.fpt.officelink.entity.SurveyQuestion;
import com.nimbusds.jose.JOSEException;

public interface ReportService {

	SurveySendDetailDTO getSendDetail(int surveyId);

	DashBoardDTO getDashBoard(Integer id) throws IOException;

	String getDownLoadToken(Integer surveyId, Integer questionId) throws JOSEException;

	Optional<SurveyQuestion> getDownloadDetail(String token) throws ParseException;

}
