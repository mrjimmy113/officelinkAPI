/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.fpt.officelink.dto.ImageNewsDTO;
import com.fpt.officelink.dto.NewsDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.entity.News;

/**
 *
 * @author Thai Phu Cuong
 */
public interface NewsService {
    
    Optional<News> searchById(int id);
    
    Page<News> searchByTitleWithPagination(String term, int pageNum);
    
    boolean addNews(News news);
    
    boolean editNews(News news);
    
    boolean removeNews(int id);
    
    NewsDTO saveImageAndConvertStringToJson(String path, MultipartFile file, String string);
    
    List<ImageNewsDTO> getListNews(Page<News> news, String path);
    
    ImageNewsDTO getNews(Optional<News> news, String path);
    
    List<ImageNewsDTO> findNewsByDate(String startDate, String endDate, String path);

	PageSearchDTO<ImageNewsDTO> getLastestNewsList(int pageNum);
    
}
