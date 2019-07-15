/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.service;

import java.util.List;
import java.util.Optional;

import com.fpt.officelink.entity.Team;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Location;

/**
 *
 * @author Thai Phu Cuong
 */
public interface LocationService {
    List<Location> getLocationsByWorkplace(int workplaceId);

    Optional<Location> searchById(int id);

    Page<Location> searchWithPagination(String term, int pageNum);

    boolean addLocation(Location location);
    
    boolean editLocation(Location location);

    boolean removeLocation(int id);
    
    List<Location> getAllLocation();

	List<Location> getWorkplaceLocation();

	List<Location> getByDepartmentId(int id);


}
