package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpt.officelink.entity.Configuration;

public interface ConfigRepository extends JpaRepository<Configuration, String> {

	List<Configuration> findAllByIsDeleted(boolean isDeleted);
}
