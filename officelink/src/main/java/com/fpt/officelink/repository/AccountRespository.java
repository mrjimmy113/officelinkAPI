package com.fpt.officelink.repository;

import java.util.List;
import java.util.Optional;

import com.fpt.officelink.entity.Team;
import com.fpt.officelink.entity.Workplace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Account;

@Repository
public interface AccountRespository extends CrudRepository<Account, Integer> {
    Page<Account> findAllByFirstname(String firstname, Pageable pageable);


    Page<Account> findAllByFirstnameContainingAndIsDeleted(String firstname , Boolean isDeleted , Pageable pageable);

    @Query("SELECT t FROM Account t WHERE t.firstname LIKE %:firstName% AND t.isDeleted = :isDeleted AND t.workplace.id = :workplaceId AND t.role.id = :roleId")
    Page<Account> findAllByFirstnameAndWorkplaceAndRole(
            @Param("firstName") String firstName,
            @Param("workplaceId") Integer workplaceId,
            @Param("isDeleted") Boolean isDeleted,
    		@Param("roleId") Integer roleId,
    Pageable pageable);

	Optional<Account> findAccountByEmail(String email);

//    Optional<Account> findAccountByEmailAndWorkspacename(String email, String worksapcename);

	Optional<Account> findByEmail(String email);

	Optional<Account> findByEmailAndPassword(String email, String password);
	
	List<Account> findAllByWorkplaceIdAndIsDeleted(Integer workplaceId, boolean isDeleted); 

	@Query("SELECT a FROM Account a WHERE a.location.id = :id AND a.workplace.id = :workId AND a.isDeleted = :isDeleted")
	List<Account> findAllEmailByLocationId(@Param("id") Integer id, @Param("workId") Integer workId, @Param("isDeleted") boolean isDeleted);

	@Query("SELECT a FROM Account a JOIN a.teams t WHERE t.id = :id AND a.workplace.id = :workId AND a.isDeleted = :isDeleted")
	List<Account> findAllEmailByTeamId(@Param("id") Integer id, @Param("workId") Integer workId, @Param("isDeleted") boolean isDeleted);
	
	@Query("SELECT a FROM Account a JOIN a.teams t WHERE t.department.id = :id AND a.workplace.id = :workId AND a.isDeleted = :isDeleted")
	List<Account> findAllEmailByDepartmentId(@Param("id") Integer id, @Param("workId") Integer workId, @Param("isDeleted") boolean isDeleted);
	
	@Query("SELECT a FROM Account a JOIN a.teams t WHERE t.department.id = :depId AND a.location.id = :loId AND a.workplace.id = :workId AND a.isDeleted = :isDeleted")
	List<Account> findAllEmailByLocationIdAndDepartmentId(@Param("depId") Integer depId, @Param("loId") Integer loId, @Param("workId") Integer workId, @Param("isDeleted") boolean isDelete);
    @Query("SELECT t FROM Account t WHERE t.isDeleted = :isDeleted AND t.workplace.id = :workplaceId")
    List<Account> findAllByWorkplaceId(
            @Param("workplaceId") Integer workplaceId,
            @Param("isDeleted") Boolean isDeleted);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.workplace.id = :id AND a.isDeleted = false")
    int countByWorkplaceId(@Param("id") Integer id);


    //fAccount findAllByEmail( String email);

	@Query("SELECT a FROM Account a WHERE a.email = :email")
	Account findAllByEmail( @Param("email") String email);



}
