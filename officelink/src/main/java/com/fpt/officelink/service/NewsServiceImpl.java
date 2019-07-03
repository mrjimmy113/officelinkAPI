/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.service;

import com.fpt.officelink.dto.ImageNewsDTO;
import com.fpt.officelink.dto.NewsDTO;
import com.fpt.officelink.entity.News;
import com.fpt.officelink.repository.NewsRepository;
import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Override
    public Page<News> searchWithPagination(String term, int pageNum) {
        Pageable pageRequest = PageRequest.of(pageNum, MAXPAGESIZE);
        return newsRep.findAllByTitleContainingAndIsDeleted(term, false, pageRequest);
    }

    @Override
    public boolean addNews(News news) {
        Optional<News> title = newsRep.findByTitleAndIsDeleted(news.getTitle(), Boolean.FALSE);
        Optional<News> content = newsRep.findByContentAndIsDeleted(news.getContent(), Boolean.FALSE);
        Optional<News> shortDescription = newsRep.findByShortDescriptionAndIsDeleted(news.getShortDescription(), Boolean.FALSE);
        if (title.isPresent() || content.isPresent() || shortDescription.isPresent()) {
            return false;
        } else {
            newsRep.save(news);
            return true;
        }
    }

    @Override
    public boolean editNews(News news) {
        try {
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
        String imageContentType = FilenameUtils.getExtension(file.getOriginalFilename());
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
}
