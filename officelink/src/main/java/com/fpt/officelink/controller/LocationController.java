/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.controller;

import com.fpt.officelink.dto.LocationDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.service.LocationService;
import java.util.ArrayList;
import java.util.List;
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
            System.out.println(e.getMessage());
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
    public ResponseEntity<Integer> create(@RequestBody LocationDTO dto) {
        HttpStatus status = null;
        try {
            Location location = new Location();
            BeanUtils.copyProperties(dto, location);
            service.saveLocation(location);
            status = HttpStatus.CREATED;
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Integer>(status.value(), status);
    }

    @PutMapping
    public ResponseEntity<Integer> update(@RequestBody LocationDTO dto) {
        HttpStatus status = null;
        try {
            Location location = new Location();
            BeanUtils.copyProperties(dto, location);
            service.saveLocation(location);
            status = HttpStatus.CREATED;
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Integer>(status.value(), status);
    }

    @DeleteMapping
    public ResponseEntity<Integer> delete(@RequestParam("id") int id) {
        HttpStatus status = null;
        try {
            service.removeLocation(id);
            status = HttpStatus.OK;
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<Integer>(status.value(), status);
    }

}
