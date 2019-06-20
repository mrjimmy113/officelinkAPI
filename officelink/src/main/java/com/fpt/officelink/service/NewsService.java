/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.service;

import com.fpt.officelink.dto.ImageNewsDTO;
import com.fpt.officelink.dto.NewsDTO;
import com.fpt.officelink.entity.News;
import java.io.OutputStream;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Thai Phu Cuong
 */
public interface NewsService {
    
    Page<News> searchWithPagination(String term, int pageNum);
    
    boolean addNews(News news);
    
    boolean editNews(News news);
    
    boolean removeNews(int id);
    
    NewsDTO saveImageAndConvertStringToJson(String path, MultipartFile file, String string);
    
    List<ImageNewsDTO> getListNews(Page<News> news, String path);
    
}
