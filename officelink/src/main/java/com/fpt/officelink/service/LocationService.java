/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.service;

import com.fpt.officelink.entity.Location;
import org.springframework.data.domain.Page;

/**
 *
 * @author Thai Phu Cuong
 */
public interface LocationService {

    Page<Location> searchWithPagination(String term, int pageNum);

    boolean addLocation(Location location);
    
    boolean editLocation(Location location);

    boolean removeLocation(int id);

}
