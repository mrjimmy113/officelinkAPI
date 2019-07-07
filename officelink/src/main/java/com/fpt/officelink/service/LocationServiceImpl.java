/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.service;

import com.fpt.officelink.entity.Department;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.repository.DepartmentRepository;
import com.fpt.officelink.repository.LocationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thai Phu Cuong
 */
@Service
public class LocationServiceImpl implements LocationService {

    private static final int MAXPAGESIZE = 9;

    @Autowired
    LocationRepository locationRep;

    @Autowired
    DepartmentRepository departmentRep;

    
    @Override
    public List<Location> getAllLocation() {
        List<Location> result = locationRep.findAllByIsDeleted(false);
        return result;
    }

    @Override
    public Department getDepartmentById(int depId) {
        return departmentRep.findDepartmentById(depId);
    }

    @Override
    public Page<Location> searchWithPagination(String term, int pageNum) {
        Pageable pageRequest = PageRequest.of(pageNum, MAXPAGESIZE);
        return locationRep.findAllByAddressContainingAndIsDeleted(term, false, pageRequest);
    }

    @Override
    public boolean addLocation(Location location) {
        Optional<Location> loc = locationRep.findByAddressAndCountyAndCityAndIsDeleted(location.getAddress(), location.getCounty(), location.getCity(), Boolean.FALSE);
        if (loc.isPresent()) {
            return false;
        } else {
            locationRep.save(location);
            return true;
        }
    }

    @Override
    public boolean editLocation(Location location) {
        try {
            locationRep.save(location);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeLocation(int id) {
        Location loc = locationRep.findById(id).get();
        if (loc != null) {
            try {
                loc.setIsDeleted(true);
                locationRep.save(loc);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

}
