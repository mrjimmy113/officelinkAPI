/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fpt.officelink.dto.ImageNewsDTO;
import com.fpt.officelink.dto.NewsDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.News;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.repository.NewsRepository;
import com.google.gson.Gson;

/**
 *
 * @author Thai Phu Cuong
 */
@Service
public class NewsServiceImpl implements NewsService {

    private static final int MAXPAGESIZE = 9;
    public static final String IMAGE_FOLDER = "image";

    @Autowired
    NewsRepository newsRep;

    @Autowired
    ServletContext context;

    private CustomUser getUserContext() {
        return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public Optional<News> searchById(int id) {
        return newsRep.findById(id);
    }

    @Override
    public Page<News> searchByTitleWithPagination(String term, int pageNum) {
        Pageable pageRequest = PageRequest.of(pageNum, MAXPAGESIZE);
        return newsRep.findAllByTitleContainingAndIsDeleted(term, false, getUserContext().getWorkplaceId(), pageRequest);
    }

    @Override
    public boolean addNews(News news) {
        try {
            Workplace workplace = new Workplace();
            workplace.setId(getUserContext().getWorkplaceId());
            news.setWorkplace(workplace);
            newsRep.save(news);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean editNews(News news) {
        try {
            Workplace workplace = new Workplace();
            workplace.setId(getUserContext().getWorkplaceId());
            news.setWorkplace(workplace);
            newsRep.save(news);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeNews(int id) {
        News news = newsRep.findById(id).get();
        if (news != null) {
            try {
                news.setIsDeleted(true);
                news.setDateDeleted(Date.from(Instant.now()));
                newsRep.save(news);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public NewsDTO saveImageAndConvertStringToJson(String path, MultipartFile file, String string) {
        Gson g = new Gson();
        NewsDTO newsDTO = g.fromJson(string, NewsDTO.class);
        String imageName = String.valueOf(file.hashCode());
        String imageFolder = path + IMAGE_FOLDER;
        String imagePath = path + IMAGE_FOLDER + "/" + imageName + "." + "jpg";
        try {
            File image = new File(imageFolder);
            boolean isCreateFolder = image.mkdir();
            image = new File(imagePath);
            if (!image.exists()) {
                image.createNewFile();
            }
            file.transferTo(image);
            BufferedImage bi = ImageIO.read(image);
            ImageIO.write(bi, "jpg", image);
            newsDTO.setImage(imageName + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsDTO;
    }

    @Override
    public List<ImageNewsDTO> getListNews(Page<News> news, String path) {
        List<ImageNewsDTO> listNews = new ArrayList<>();
        news.getContent().forEach(element -> {
            try {
                ImageNewsDTO dto = new ImageNewsDTO();
                BeanUtils.copyProperties(element, dto);
                String tmp = path + "image\\" + dto.getImage();
                Path byteImage = Paths.get(tmp);
                byte[] data = Files.readAllBytes(byteImage);
                dto.setByte_image(data);
                listNews.add(dto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return listNews;
    }

    @Override
    public ImageNewsDTO getNews(Optional<News> opt, String path) {
        ImageNewsDTO dto = new ImageNewsDTO();
        News news = opt.get();
        BeanUtils.copyProperties(news, dto);
        try {
            String tmp = path + "image\\" + dto.getImage();
            Path byteImage = Paths.get(tmp);
            byte[] data = Files.readAllBytes(byteImage);
            dto.setByte_image(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    @Override
    public List<ImageNewsDTO> findNewsByDate(String startDate, String endDate, String path) {
        List<ImageNewsDTO> listNews = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        List<News> news = newsRep.findByIsDeleted(false);
        try {
            c.setTime(sdf.parse(endDate));
            c.add(Calendar.DATE, 1);
            news = newsRep.findNewstByDateCreated(sdf.parse(startDate), c.getTime(), getUserContext().getWorkplaceId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        news.forEach(element -> {
            try {
                ImageNewsDTO dto = new ImageNewsDTO();
                BeanUtils.copyProperties(element, dto);
                String tmp = path + "image\\" + dto.getImage();
                Path byteImage = Paths.get(tmp);
                byte[] data = Files.readAllBytes(byteImage);
                dto.setByte_image(data);
                listNews.add(dto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return listNews;
    }

    @Override
    public PageSearchDTO<ImageNewsDTO> getLastestNewsList(int pageNum) {
        PageSearchDTO<ImageNewsDTO> result = new PageSearchDTO<ImageNewsDTO>();
        List<ImageNewsDTO> listNews = new ArrayList<>();
        Pageable pageRequest = PageRequest.of(pageNum, MAXPAGESIZE);
        Page<News> news = newsRep.findLastestNews(getUserContext().getWorkplaceId(), pageRequest);
        news.getContent().forEach(element -> {
            try {
                ImageNewsDTO dto = new ImageNewsDTO();
                BeanUtils.copyProperties(element, dto);
                String tmp = context.getRealPath("") + "image\\" + dto.getImage();
                Path byteImage = Paths.get(tmp);
                byte[] data = Files.readAllBytes(byteImage);
                dto.setByte_image(data);
                listNews.add(dto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        result.setMaxPage(news.getTotalPages());
        result.setObjList(listNews);
        return result;
    }
}
