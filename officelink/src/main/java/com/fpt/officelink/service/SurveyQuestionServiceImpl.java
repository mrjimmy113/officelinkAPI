/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.service;

import com.fpt.officelink.FileStorageProperties;
import com.fpt.officelink.entity.Answer;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.repository.SurveyQuestionRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thai Phu Cuong
 */
@Service
public class SurveyQuestionServiceImpl implements SurveyQuestionService {

    private static int count = 0;
    private final Path fileStorageLocation;

    @Autowired
    SurveyQuestionRepository surveyQuestionRep;

    @Autowired
    public SurveyQuestionServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Resource loadFileAsResource(String fileName, int id) {
        deleteExcel();
        createExcel(fileName, id);
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new Exception("File not found " + fileName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void deleteExcel() {
        File directory = new File("C:\\Users\\Thai Phu Cuong\\Desktop\\GitHub\\officelinkAPI\\officelink\\Excel");
        File[] files = directory.listFiles();
        for (File file : files) {
            if (!file.delete()) {
                System.out.println("Failed to delete " + file);
            }
        }
    }

    public void createExcel(String fileName, int id) {
        count = 0;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();
            //Create First Row
            XSSFRow row1 = sheet.createRow(count);
            count++;
            XSSFCell r1c1 = row1.createCell(0);
            r1c1.setCellValue("No.");
            XSSFCell r1c2 = row1.createCell(1);
            r1c2.setCellValue("Question");
            XSSFCell r1c3 = row1.createCell(2);
            r1c3.setCellValue("Answer");
            //Insert data 
            List<SurveyQuestion> surveyQuestionList = surveyQuestionRep.findAllBySurveyId(id);
            for (int i = 0; i < surveyQuestionList.size(); i++) {
                XSSFRow tmp1 = sheet.createRow(count);
                count++;
                XSSFCell cell1 = tmp1.createCell(0);
                cell1.setCellValue(i + 1);
                XSSFCell cell2 = tmp1.createCell(1);
                cell2.setCellValue(surveyQuestionList.get(i).getQuestion().getQuestion());
                Set<Answer> answerList = surveyQuestionList.get(i).getAnswers();
                for (Answer answer : answerList) {
                    XSSFRow tmp2 = sheet.createRow(count);
                    count++;
                    XSSFCell cell3 = tmp2.createCell(2);
                    cell3.setCellValue(answer.getContent());
                }
            }
            //close
            FileOutputStream fos = new FileOutputStream(new File("Excel\\" + fileName));
            workbook.write(fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
