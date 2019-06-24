package com.fpt.officelink.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.WordCloudFilter;
@Repository
public interface WordCloudFilterRepository extends JpaRepository<WordCloudFilter, Integer>{
	Page<WordCloudFilter> findAllByNameContainingAndIsDeleted(String name, boolean isDeleted,Pageable page);
	Optional<WordCloudFilter> findByNameInIgnoreCase(String name);
	Optional<WordCloudFilter> findByNameInIgnoreCaseAndLanguageInIgnoreCase(String name, String language);
}
