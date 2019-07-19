package com.fpt.officelink.service;

import java.io.IOException;

import com.fpt.officelink.dto.DashBoardDTO;
import com.fpt.officelink.dto.SurveySendDetailDTO;

public interface ReportService {

	SurveySendDetailDTO getSendDetail(int surveyId);

	DashBoardDTO getDashBoard(Integer id) throws IOException;

}
