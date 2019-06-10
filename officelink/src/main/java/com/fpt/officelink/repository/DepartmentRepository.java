package com.fpt.officelink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
	Page<Department> findAllByNameContainingAndIsDeleted(String name, Boolean isDeleted, Pageable page);

	// for create new
	Optional<Department> findByNameAndIsDeleted(String name, Boolean isDeleted);
	//
	List<Department> findAllByIsDeleted(Boolean isDeleted); 
	//
	@Query(value = "SELECT d FROM Department d left join fetch d.teams t WHERE d.id = :depId AND t.isDeleted= false")
	Department getDepartmentWithTeam(@Param("depId") Integer depId);
}
