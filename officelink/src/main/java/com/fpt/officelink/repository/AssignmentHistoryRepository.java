package com.fpt.officelink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.AssignmentHistory;

@Repository
public interface AssignmentHistoryRepository extends JpaRepository<AssignmentHistory, Integer>{
//	Optional<AssignmentHistory> findFirstByAccountIdOrderByDateCreatedDesc(Integer accountId);
}
