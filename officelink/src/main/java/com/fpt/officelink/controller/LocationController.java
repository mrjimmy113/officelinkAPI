/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.controller;

import com.fpt.officelink.dto.LocationDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.entity.Department;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.service.LocationService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Thai Phu Cuong
 */
@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    LocationService service;

    @GetMapping(value = "/listAll")
    public ResponseEntity<List<LocationDTO>> getAllLocation() {
        HttpStatus status = null;
        List<LocationDTO> res = new ArrayList<LocationDTO>();

        try {
            //
            List<Location> result = service.getAllLocation();
            //
            List<LocationDTO> resultList = new ArrayList<LocationDTO>();
            result.forEach(element -> {
                LocationDTO dto = new LocationDTO();
                BeanUtils.copyProperties(element, dto);
                resultList.add(dto);
            });
            //
            res.addAll(resultList);
            status = HttpStatus.OK;
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<List<LocationDTO>>(res, status);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<LocationDTO>> getLocationByDepartmentId(@RequestParam("depId") Integer depId) {
        HttpStatus status = null;
        List<LocationDTO> res = new ArrayList<LocationDTO>();

        try {
            //
            Department dep = service.getDepartmentById(depId);
            List<Location> loc = new ArrayList<>();
            //		
            BeanUtils.copyProperties(loc, res);
            dep.getLocations().forEach(l -> {
                LocationDTO locationDTO = new LocationDTO();
                BeanUtils.copyProperties(l, locationDTO);
                if (locationDTO.isIsDeleted() == false) {
                    res.add(locationDTO);
                }
            });
            BeanUtils.copyProperties(loc, res);
            status = HttpStatus.OK;
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<List<LocationDTO>>(res, status);
    }

    @GetMapping
    public ResponseEntity<PageSearchDTO<LocationDTO>> search(@RequestParam("term") String term) {
        HttpStatus status = null;
        PageSearchDTO<LocationDTO> res = new PageSearchDTO<LocationDTO>();
        try {
            //Call Service
            Page<Location> result = service.searchWithPagination(term, 0);
            //Convert to DTO
            List<LocationDTO> resultList = new ArrayList<LocationDTO>();
            result.getContent().forEach(element -> {
                LocationDTO dto = new LocationDTO();
                BeanUtils.copyProperties(element, dto);
                resultList.add(dto);
            });
            res.setMaxPage(result.getTotalPages());
            res.setObjList(resultList);
            status = HttpStatus.OK;
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<PageSearchDTO<LocationDTO>>(res, status);
    }

    @GetMapping(value = "/getPage")
    public ResponseEntity<PageSearchDTO<LocationDTO>> searchGetPage(@RequestParam("term") String term, @RequestParam("page") int page) {
        HttpStatus status = null;
        PageSearchDTO<LocationDTO> res = new PageSearchDTO<LocationDTO>();
        try {
            //Call Service
            Page<Location> result = service.searchWithPagination(term, page);
            //Convert to DTO
            List<LocationDTO> resultList = new ArrayList<LocationDTO>();
            result.getContent().forEach(element -> {
                LocationDTO dto = new LocationDTO();
                BeanUtils.copyProperties(element, dto);
                resultList.add(dto);
            });
            res.setMaxPage(result.getTotalPages());
            res.setObjList(resultList);
            status = HttpStatus.OK;
        } catch (Exception e) {

            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<PageSearchDTO<LocationDTO>>(res, status);
    }

    @PostMapping
    public ResponseEntity<Integer> add(@RequestBody LocationDTO dto) {
        HttpStatus status = null;
        try {
            Location location = new Location();
            BeanUtils.copyProperties(dto, location);
            if (service.addLocation(location)) {
                status = HttpStatus.CREATED;
            } else {
                status = HttpStatus.CONFLICT;
            }

        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<Integer>(status.value(), status);
    }

    @PutMapping
    public ResponseEntity<Integer> edit(@RequestBody LocationDTO dto) {
        HttpStatus status = null;
        try {
            Location location = new Location();
            BeanUtils.copyProperties(dto, location);
            if (service.editLocation(location)) {
                status = HttpStatus.OK;
            } else {
                status = HttpStatus.CONFLICT;
            }

        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Integer>(status.value(), status);
    }

    @DeleteMapping
    public ResponseEntity<Integer> remove(@RequestParam("id") int id) {
        HttpStatus status = null;
        try {
            if (service.removeLocation(id)) {
                status = HttpStatus.OK;
            } else {
                status = HttpStatus.CONFLICT;
            }

        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<Integer>(status.value(), status);
    }

}
