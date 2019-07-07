package com.fpt.officelink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Workplace;

@Repository
public interface WorkplaceRepository extends JpaRepository<Workplace, Integer> {
	Page<Workplace> findAllByNameContainingAndIsDeleted(String name, Boolean isDeleted, Pageable page);

	// for create new
	Optional<Workplace> findByNameAndIsDeleted(String name, Boolean isDeleted);
	//
	List<Workplace> findAllByIsDeleted(Boolean isDeleted); 
}
