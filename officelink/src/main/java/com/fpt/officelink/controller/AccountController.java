package com.fpt.officelink.controller;


import com.fpt.officelink.dto.AccountDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.service.AccountService;
import com.fpt.officelink.service.JwtService;
import com.fpt.officelink.service.SendMail;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    AccountService service;

    @Autowired
    SendMail sendMail;


    @Autowired
    JwtService jwt;


    @GetMapping
    public ResponseEntity<PageSearchDTO<AccountDTO>> searchWithTerm(@RequestParam("term") String term){
        HttpStatus status = null;

        PageSearchDTO<AccountDTO> pageSearchDTO = new PageSearchDTO<>();

        try{
            Page<Account>  pageAccount = service.searchWithPagination(term, 0);

            List<AccountDTO> listAccount = new ArrayList<AccountDTO>();


            pageAccount.getContent().forEach(element -> {
                AccountDTO accountDTO = new AccountDTO();

                BeanUtils.copyProperties(element,accountDTO);

                listAccount.add(accountDTO);

            });
            pageSearchDTO.setMaxPage(pageAccount.getTotalPages());
            pageSearchDTO.setObjList(listAccount);
            status = HttpStatus.OK;

        }catch (Exception ex){

            status = HttpStatus.BAD_REQUEST;
            ex.printStackTrace();
        }

        return new ResponseEntity<PageSearchDTO<AccountDTO>>(pageSearchDTO,status);
    }

    @GetMapping(value = "/getAccountByEmail")
    public ResponseEntity<Optional<Account>> getAccountByEmail(@RequestParam("emailToken") String emailToken){
        HttpStatus status = null;
        Optional<Account> account = null;

        try{
            String email = jwt.getEmailFromToken(emailToken);

            account = service.getAccountByEmail(email);
            status = HttpStatus.OK;

        }catch (Exception ex){
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<Optional<Account>>(account, status);
    }


    @GetMapping(value = "/getAccount")
    public ResponseEntity<PageSearchDTO<AccountDTO>> searchGetPage(@RequestParam("term") String term, @RequestParam("page") int page){
        HttpStatus status = null;

        PageSearchDTO<AccountDTO> pageSearchDTO = new PageSearchDTO<>();

        try{
            Page<Account>  pageAccount = service.searchWithPagination(term, page);

            List<AccountDTO> listAccount = new ArrayList<AccountDTO>();


            pageAccount.getContent().forEach(element -> {
                AccountDTO accountDTO = new AccountDTO();

                BeanUtils.copyProperties(element,accountDTO);

                listAccount.add(accountDTO);

            });
            pageSearchDTO.setMaxPage(pageAccount.getTotalPages());
            pageSearchDTO.setObjList(listAccount);
            status = HttpStatus.OK;

        }catch (Exception ex){

            status = HttpStatus.BAD_REQUEST;

        }

        return new ResponseEntity<PageSearchDTO<AccountDTO>>(pageSearchDTO,status);
    }





    @PostMapping
    public ResponseEntity<Number> create(@RequestBody AccountDTO dto) {
        HttpStatus status = null;


        try {
            Account entity = new Account();

            BeanUtils.copyProperties(dto, entity);
            boolean res = service.addNewAccount(entity);
            if(res){

                status = HttpStatus.OK;
            }else {
                status = HttpStatus.CONFLICT;
            }

        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
            e.printStackTrace();

        }
        return new ResponseEntity<Number>(status.value(), status);
    }

    @GetMapping(value = "/sendMail")
    public ResponseEntity<Integer> sendMail(@RequestParam("emailTo") String[] emailTo, @RequestParam("role") String role){
        HttpStatus status = null;
        try {
            String token = null;
            for (int i = 0 ; i < emailTo.length; i++){
                token = jwt.createTokenWithEmail(emailTo[i]);
            }

            if(role.contentEquals("employee")){
                String contentMess = "Please go http://localhost:4200/join";
                sendMail.sendMail(emailTo, contentMess);
            }
            else{
                String contentMess = "Please go http://localhost:4200/login-form/" + token + " to join ";


                sendMail.sendMail(emailTo, contentMess);
            }

            System.out.println(role);
            status = HttpStatus.OK;
        }catch (Exception ex){
            status = HttpStatus.BAD_REQUEST;
        }


        return new ResponseEntity<Integer>(status.value(), status);
    }


    @PutMapping
    public ResponseEntity<Integer> update(@RequestBody AccountDTO dto) {
        HttpStatus status = null;
        try {
            Account entity = new Account();
            BeanUtils.copyProperties(dto, entity);
            service.modifyAccount(entity);
            status = HttpStatus.CREATED;
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
            e.printStackTrace();
        }

        return new ResponseEntity<Integer>(status.value(), status);
    }




    @DeleteMapping
    public ResponseEntity<Integer> delete(@RequestParam("id") int id){
        HttpStatus status = null;
        try{
            service.removeAccount(id);
            status = HttpStatus.OK;

        }catch (Exception ex){
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Integer>(status.value(), status);
    }




}