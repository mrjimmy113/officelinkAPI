package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fpt.officelink.entity.Configuration;

public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {
	Page<Configuration> findAllByWorkplaceIdAndIsDeleted(int workpId, Boolean isDeleted, Pageable page);
	
	List<Configuration> findAllByIsDeleted(boolean isDeleted);
}
