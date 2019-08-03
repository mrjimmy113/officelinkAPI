package com.fpt.officelink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.TeamQuestionReport;

@Repository
public interface TeamQuestionReportRepository extends JpaRepository<TeamQuestionReport, Integer> {
	
}
