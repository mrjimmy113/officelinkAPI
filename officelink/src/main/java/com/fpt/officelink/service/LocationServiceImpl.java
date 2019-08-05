/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.repository.DepartmentRepository;
import com.fpt.officelink.repository.LocationRepository;

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
	
	private CustomUser getUserContext() {
    	return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


    @Override
    public List<Location> getLocationsByWorkplace(int workplaceId) {
        return locationRep.findAllByWorkplaceId(workplaceId, false);
    }

    @Override
    public Optional<Location> searchById(int id) {
        return locationRep.findById(id);
    }

	@Override
	public List<Location> getAllLocation() {
		List<Location> result = locationRep.findAllByWorkplaceIdAndIsDeleted(
				((CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getWorkplaceId(),
				false);
		System.out.print(result.size());
		return result;
	}

    @Override
    public Page<Location> searchWithPagination(String term, int pageNum) {
        Pageable pageRequest = PageRequest.of(pageNum, MAXPAGESIZE);
        return locationRep.findAllByNameContainingAndIsDeletedAndWorkplaceId(term, false, getUserContext().getWorkplaceId(), pageRequest);
    }

	@Override
	public boolean addLocation(Location location) {
		Optional<Location> loc1 = locationRep.findByNameContainingAndIsDeletedAndWorkplaceId(location.getName(), Boolean.FALSE, getUserContext().getWorkplaceId());
                Optional<Location> loc2 = locationRep.findByAddressContainingAndIsDeletedAndWorkplaceId(location.getAddress(), Boolean.FALSE, getUserContext().getWorkplaceId());
                if (loc1.isPresent() || loc2.isPresent()) {
                    return false;
                } else {
                    Workplace workplace = new Workplace();
                    workplace.setId(getUserContext().getWorkplaceId());
                    location.setWorkplace(workplace);
                    locationRep.save(location);
                    return true;
                }
	}

    boolean check;

    @Override
    public boolean editLocation(Location location) {
        check = true;
        List<Location> loc = locationRep.findAllByWorkplaceIdAndIsDeleted(getUserContext().getWorkplaceId(), false);
        loc.forEach(element -> {
            if (element.getId().intValue() != location.getId().intValue()) {
                if (element.getName().contains(location.getName()) || element.getAddress().contains(location.getAddress())) {
                    check = false;
                }
            }
        });
        if (check == false) {
            return false;
        } else {
            Workplace workplace = new Workplace();
            workplace.setId(getUserContext().getWorkplaceId());
            location.setWorkplace(workplace);
            locationRep.save(location);
            return true;
        }
    }

	@Override
	public boolean removeLocation(int id) {
		Location loc = locationRep.findById(id).get();
		if (loc != null) {
			try {
				loc.setIsDeleted(true);
				loc.setDateDeleted(Date.from(Instant.now()));
				locationRep.save(loc);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<Location> getWorkplaceLocation() {
		return locationRep.findAllByWorkplaceIdAndIsDeleted(
				((CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getWorkplaceId(),
				false);
	}

	@Override
	public List<Location> getByDepartmentId(int id) {
		return new ArrayList<Location>(locationRep.findAllByDepartmentId(id));
	}

        

}
