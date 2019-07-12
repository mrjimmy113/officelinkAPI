package com.fpt.officelink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpt.officelink.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
	
	@Query("SELECT COUNT(a) FROM Answer a WHERE a.account.id = :id")
	int countAnswerByAccountId(@Param("id") int id);
}
