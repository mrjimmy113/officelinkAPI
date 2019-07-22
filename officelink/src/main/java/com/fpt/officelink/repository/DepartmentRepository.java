package com.fpt.officelink.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
	Page<Department> findAllByNameContainingAndIsDeletedAndWorkplaceId(String name, Boolean isDeleted, int workplaceId, Pageable page);

	// for create new
	Optional<Department> findByNameAndIsDeletedAndWorkplaceId(String name, Boolean isDeleted, int workplaceId);
	//
	List<Department> findAllByWorkplaceIdAndIsDeleted(int workplaceId, Boolean isDeleted); 
	//
	@Query(value = "SELECT d FROM Department d left join fetch d.teams t WHERE d.id = :depId AND t.isDeleted= false")
	Department getDepartmentWithTeam(@Param("depId") int depId);
	
	@Query(value= "SELECT d FROM Department d WHERE d.id = ?1")
    Department findDepartmentById(int depId) ;
	
	@Query("SELECT t.department FROM Team t JOIN t.accounts a WHERE a.location.id = :id")
	Set<Department> findAllByLocationId(@Param("id") Integer id);
	
	@Query("SELECT COUNT(d) FROM Department d WHERE d.workplace.id = :id AND d.isDeleted = false")
	int countDepartmentOnWorkplace(@Param("id") Integer id);
	
	@Query("SELECT t.department FROM Team t JOIN t.accounts a WHERE a.id =:id")
	List<Department> findByAccountId(@Param("id") Integer id);
}
