package com.fpt.officelink.service;

import java.util.concurrent.Future;

public interface TeamReportService {
	Future<Boolean> generateTeamQuestionReport(int surveyId);
}
