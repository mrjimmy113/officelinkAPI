package com.fpt.officelink.service;

import com.fpt.officelink.dto.DashBoardDTO;
import com.fpt.officelink.dto.SurveySendDetailDTO;

public interface ReportService {

	SurveySendDetailDTO getSendDetail(int surveyId);

	DashBoardDTO getDashBoard(Integer id);

}
