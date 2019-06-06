package com.fpt.officelink.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Department;

@Repository
public interface DepartmentRespository extends JpaRepository<Department, Integer> {
	Page<Department> findAllByNameContainingAndIsDeleted(String name, Boolean isDeleted, Pageable page);

	// for create new
	Optional<Department> findByNameAndIsDeleted(String name, Boolean isDeleted);
}
