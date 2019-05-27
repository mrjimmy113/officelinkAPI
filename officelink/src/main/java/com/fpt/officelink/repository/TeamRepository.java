package com.fpt.officelink.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer>{
	Page<Team> findAllByName(String name, PageRequest pageRequest);
}
