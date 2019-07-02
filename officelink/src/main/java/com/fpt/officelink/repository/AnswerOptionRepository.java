package com.fpt.officelink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.AnswerOption;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Integer>{

}
