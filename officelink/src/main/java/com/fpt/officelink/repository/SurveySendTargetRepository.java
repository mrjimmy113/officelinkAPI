package com.fpt.officelink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.SurveySendTarget;

@Repository
public interface SurveySendTargetRepository extends JpaRepository<SurveySendTarget, Integer>{

}
