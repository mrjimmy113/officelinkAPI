/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.controller;

import com.fpt.officelink.dto.ImageNewsDTO;
import com.fpt.officelink.dto.NewsDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.entity.News;
import com.fpt.officelink.service.NewsService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping
    public ResponseEntity<PageSearchDTO<ImageNewsDTO>> search(@RequestParam("term") String term) {
        HttpStatus status = null;
        PageSearchDTO<ImageNewsDTO> res = new PageSearchDTO<ImageNewsDTO>();
        try {
            //Call Service
            Page<News> result = service.searchWithPagination(term, 0);
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
    public ResponseEntity<PageSearchDTO<NewsDTO>> searchGetPage(@RequestParam("term") String term, @RequestParam("page") int page) {
        HttpStatus status = null;
        PageSearchDTO<NewsDTO> res = new PageSearchDTO<NewsDTO>();
        try {
            //Call Service
            Page<News> result = service.searchWithPagination(term, page);
            //Convert to DTO
            List<NewsDTO> resultList = new ArrayList<NewsDTO>();
            result.getContent().forEach(element -> {
                NewsDTO dto = new NewsDTO();
                BeanUtils.copyProperties(element, dto);
                resultList.add(dto);
            });
            res.setMaxPage(result.getTotalPages());
            res.setObjList(resultList);
            status = HttpStatus.OK;
        } catch (Exception e) {

            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<PageSearchDTO<NewsDTO>>(res, status);
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Integer> addNews(@RequestParam("file") MultipartFile file, @RequestParam("dto") String stringDTO) {
        System.out.println(stringDTO);
        HttpStatus status = null;
        try {
            if (file == null || stringDTO == null) {
                status = HttpStatus.NO_CONTENT;
            } else {
                News news = new News();
                String path = context.getRealPath("");
                NewsDTO newsDTO = service.saveImageAndConvertStringToJson(path, file, stringDTO);
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

    @PutMapping
    public ResponseEntity<Integer> editNews(@RequestParam("file") MultipartFile file, @RequestParam("dto") String stringDTO) {
        System.out.println(stringDTO);
        HttpStatus status = null;
        try {
            if (file == null || stringDTO == null) {
                status = HttpStatus.NO_CONTENT;
            } else {
                News news = new News();
                String path = context.getRealPath("");
                
                NewsDTO newsDTO = service.saveImageAndConvertStringToJson(path, file, stringDTO);
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

    @DeleteMapping
    public ResponseEntity<Integer> remove(@RequestParam("id") int id) {
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
}
