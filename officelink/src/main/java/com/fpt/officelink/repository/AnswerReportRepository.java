package com.fpt.officelink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.AnswerReport;

@Repository
public interface AnswerReportRepository extends JpaRepository<AnswerReport, Integer> {

}
