/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.service;

import org.springframework.core.io.Resource;

/**
 *
 * @author Thai Phu Cuong
 */
public interface SurveyQuestionService {
    Resource loadFileAsResource(String fileName, int id);
}
