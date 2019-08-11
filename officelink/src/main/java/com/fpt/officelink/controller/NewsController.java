/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fpt.officelink.dto.ImageNewsDTO;
import com.fpt.officelink.dto.NewsDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.entity.News;
import com.fpt.officelink.service.NewsService;
import com.google.gson.Gson;

/**
 *
 * @author Thai Phu Cuong
 */
@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    NewsService service;

    @Autowired
    ServletContext context;

    @GetMapping(value = "/getDate")
    public ResponseEntity<List<ImageNewsDTO>> searchByDate(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        HttpStatus status = null;
        List<ImageNewsDTO> res = new ArrayList<>();
        try {
            String path = context.getRealPath("");
            res = service.findNewsByDate(startDate, endDate, path);
            status = HttpStatus.OK;
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<List<ImageNewsDTO>>(res, status);
    }

    @GetMapping(value = "/getId")
    public ResponseEntity<ImageNewsDTO> searchById(@RequestParam("id") int id) {
        HttpStatus status = null;
        ImageNewsDTO res = new ImageNewsDTO();
        try {
            Optional<News> result = service.searchById(id);
            String path = context.getRealPath("");
            res = service.getNews(result, path);
            status = HttpStatus.OK;
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<ImageNewsDTO>(res, status);
    }

    @GetMapping
    public ResponseEntity<PageSearchDTO<ImageNewsDTO>> searchByTitle(@RequestParam("term") String term) {
        HttpStatus status = null;
        PageSearchDTO<ImageNewsDTO> res = new PageSearchDTO<ImageNewsDTO>();
        try {
            //Call Service
            Page<News> result = service.searchByTitleWithPagination(term, 0);
            //Convert to DTO
            String path = context.getRealPath("");
            List<ImageNewsDTO> resultList = service.getListNews(result, path);
            res.setMaxPage(result.getTotalPages());
            res.setObjList(resultList);
            status = HttpStatus.OK;
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<PageSearchDTO<ImageNewsDTO>>(res, status);
    }

    @GetMapping(value = "/getPage")
    public ResponseEntity<PageSearchDTO<ImageNewsDTO>> searchGetPage(@RequestParam("term") String term, @RequestParam("page") int page) {
        HttpStatus status = null;
        PageSearchDTO<ImageNewsDTO> res = new PageSearchDTO<ImageNewsDTO>();
        try {
            //Call Service
            Page<News> result = service.searchByTitleWithPagination(term, page);
            //Convert to DTO
            String path = context.getRealPath("");
            List<ImageNewsDTO> resultList = service.getListNews(result, path);
            res.setMaxPage(result.getTotalPages());
            res.setObjList(resultList);
            status = HttpStatus.OK;
        } catch (Exception e) {

            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<PageSearchDTO<ImageNewsDTO>>(res, status);
    }

    @Secured({"ROLE_employer","ROLE_system_admin"})
    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Integer> addNews(@RequestParam("file") MultipartFile file, @RequestParam("dto") String stringDTO) {
        HttpStatus status = null;
        try {
            if (file == null || stringDTO == null) {
                status = HttpStatus.NO_CONTENT;
            } else {
                News news = new News();
                String path = context.getRealPath("");
                NewsDTO newsDTO = service.saveImageAndConvertStringToJson(path, file, stringDTO);
                newsDTO.setDateCreated(Date.from(Instant.now()));
                newsDTO.setDateModified(Date.from(Instant.now()));
                BeanUtils.copyProperties(newsDTO, news);
                if (service.addNews(news)) {
                    status = HttpStatus.CREATED;
                } else {
                    status = HttpStatus.CONFLICT;
                }
            }
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<Integer>(status.value(), status);
    }

    @Secured({"ROLE_employer","ROLE_system_admin"})
    @PutMapping(value = "/edit")
    public ResponseEntity<Integer> editNewsNotHasFile(@RequestParam("dto") String stringDTO) {
        HttpStatus status = null;
        try {
            if (stringDTO == null) {
                status = HttpStatus.NO_CONTENT;
            } else {

                News news = new News();
                Gson g = new Gson();
                NewsDTO newsDTO = g.fromJson(stringDTO, NewsDTO.class);
                newsDTO.setDateModified(Date.from(Instant.now()));
                BeanUtils.copyProperties(newsDTO, news);
                if (service.editNews(news)) {
                    status = HttpStatus.OK;
                } else {
                    status = HttpStatus.CONFLICT;
                }

            }
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Integer>(status.value(), status);
    }

    @Secured({"ROLE_employer","ROLE_system_admin"})
    @PutMapping()
    public ResponseEntity<Integer> editNews(@RequestParam("file") MultipartFile file, @RequestParam("dto") String stringDTO) {
        HttpStatus status = null;
        try {
            if (stringDTO == null) {
                status = HttpStatus.NO_CONTENT;
            } else {

                News news = new News();
                String path = context.getRealPath("");

                NewsDTO newsDTO = service.saveImageAndConvertStringToJson(path, file, stringDTO);
                newsDTO.setDateModified(Date.from(Instant.now()));
                BeanUtils.copyProperties(newsDTO, news);
                if (service.editNews(news)) {
                    status = HttpStatus.OK;
                } else {
                    status = HttpStatus.CONFLICT;
                }

            }
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Integer>(status.value(), status);
    }

    @Secured({"ROLE_employer","ROLE_system_admin"})
    @DeleteMapping
    public ResponseEntity<Integer> removeNews(@RequestParam("id") int id) {
        HttpStatus status = null;
        try {
            if (service.removeNews(id)) {
                status = HttpStatus.OK;
            } else {
                status = HttpStatus.CONFLICT;
            }

        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<Integer>(status.value(), status);
    }
    
    @GetMapping("/lastest")
    public ResponseEntity<PageSearchDTO<ImageNewsDTO>> getLastest(@RequestParam("page") int pageNum) {
    	PageSearchDTO<ImageNewsDTO> res = new PageSearchDTO<ImageNewsDTO>();
    	HttpStatus status = null;
    	try {
			res = service.getLastestNewsList(pageNum);
    		status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
    	
    	return new ResponseEntity<PageSearchDTO<ImageNewsDTO>>(res,status);
    }
}
