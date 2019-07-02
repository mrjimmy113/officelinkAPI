package com.fpt.officelink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.TypeQuestion;

@Repository
public interface TypeQuestionRepository  extends JpaRepository<TypeQuestion, Integer>{

}
