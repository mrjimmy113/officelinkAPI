/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.service;

import com.fpt.officelink.entity.Location;
import com.fpt.officelink.repository.LocationRepository;
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

    @Override
    public Page<Location> searchWithPagination(String term, int pageNum) {
        Pageable pageRequest = PageRequest.of(pageNum, MAXPAGESIZE);
        return locationRep.findAllByAddress(term, pageRequest);
    }

    @Override
    public void saveLocation(Location location) {
        locationRep.save(location);
    }

    @Override
    public void removeLocation(int id) {
        Location loc = locationRep.findById(id).get();
        if (loc != null) {
            loc.setIsDeleted(true);
            locationRep.save(loc);
        }
    }

}
