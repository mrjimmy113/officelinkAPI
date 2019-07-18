package com.fpt.officelink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer>{
	
	@Query("SELECT t FROM Team t WHERE t.name LIKE %:name% AND t.isDeleted = :isDeleted AND t.department.workplace.id = :workplaceId")
	Page<Team> findAllByNameContainingAndIsDeletedAndWorkplaceId(
			@Param("name") String name,
			@Param("isDeleted") boolean isDeleted,
			@Param("workplaceId") int workplaceId,
			Pageable page);
	
	Page<Team> findAllByNameContainingAndIsDeleted(String name, boolean isDeleted, Pageable page);
	// for create new
	@Query("SELECT t FROM Team t WHERE t.name LIKE :name AND t.isDeleted = :isDeleted AND t.department.workplace.id = :workplaceId")
	Optional<Team> findByNameAndIsDeleted(
			@Param("name") String name,
			@Param("workplaceId") int workplaceId,
			@Param("isDeleted") Boolean isDeleted);
	
	@Query("SELECT t FROM Team t WHERE t.department.id = :id")
	List<Team> findAllByDepartmentId(@Param("id") Integer id);
	
	@Query("SELECT t FROM Team t WHERE t.isDeleted = :isDeleted AND t.department.workplace.id = :workplaceId")
	List<Team> findAllByWorkplaceId(
			@Param("workplaceId") Integer workplaceId, 
			@Param("isDeleted") Boolean isDeleted);
	//

	List<Team> findAllByIsDeleted(Boolean isDeleted);

	@Query("SELECT t FROM Team t JOIN t.accounts a WHERE a.location.id = :id")
	List<Team> findAllByLocationId(@Param("id") Integer id);
}
