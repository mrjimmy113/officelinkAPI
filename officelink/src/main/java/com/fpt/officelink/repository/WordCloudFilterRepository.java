package com.fpt.officelink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.WordCloudFilter;

@Repository
public interface WordCloudFilterRepository extends JpaRepository<WordCloudFilter, Integer> {

	@Query("SELECT w FROM WordCloudFilter w WHERE LOWER(w.name) = LOWER(:name) AND w.workplace.id = :workplaceId")
	Optional<WordCloudFilter> findByNameInIgnoreCase(@Param("name")String name, @Param("workplaceId") Integer workplaceId);

	@Query("SELECT w FROM WordCloudFilter w WHERE( w.workplace.id = :id OR w.isTemplate = true) AND w.isDeleted = :isDeleted")
	List<WordCloudFilter> finAllByWorkplaceIdAndIsDeleted(@Param("id") Integer id,
			@Param("isDeleted") boolean isDeleted);

	@Query("SELECT w FROM WordCloudFilter w WHERE w.name LIKE %:name% AND w.workplace.id = :id AND w.isDeleted = :isDeleted ORDER BY w.dateModified DESC")
	Page<WordCloudFilter> findAllByNameContainAndWorkplaceIdAndIsDeleted(@Param("name") String name,
			@Param("id") Integer workplaceId, @Param("isDeleted") boolean isDeleted,Pageable page);

	@Query("SELECT w FROM WordCloudFilter w WHERE w.workplace.id = :workplaceId AND w.id = :id")
	Optional<WordCloudFilter> findOneById(@Param("id") Integer id,@Param("workplaceId") Integer workplaceId);
}
