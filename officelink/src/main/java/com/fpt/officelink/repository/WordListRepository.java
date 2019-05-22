package com.fpt.officelink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Word;
@Repository
public interface WordListRepository extends JpaRepository<Word, Integer> {

}
