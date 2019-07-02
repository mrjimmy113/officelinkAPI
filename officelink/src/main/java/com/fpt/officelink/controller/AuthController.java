package com.fpt.officelink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fpt.officelink.dto.AccountDTO;
import com.fpt.officelink.dto.AuthDTO;
import com.fpt.officelink.service.AccountService;

@RestController
public class AuthController {

	@Autowired
	AccountService accountSer;
	
	@PostMapping("/login")
	public ResponseEntity<AuthDTO> login(@RequestBody AccountDTO acc) {
		AuthDTO res = new AuthDTO();
		HttpStatus status = null;
		try {
			
			res = accountSer.getAuthenticationInfor(acc.getEmail(), acc.getPassword());
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<AuthDTO>(res,status);
	}
}
