package com.fpt.officelink.controller;


import com.fpt.officelink.dto.AccountDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.service.AccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    AccountService service;


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
    public ResponseEntity<Integer> create(@RequestBody AccountDTO dto) {
        HttpStatus status = null;
        try {
            Account entity = new Account();

            BeanUtils.copyProperties(dto, entity);
            service.addNewAccount(entity);

            status = HttpStatus.CREATED;
        } catch (Exception e) {
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


    @GetMapping(value = "/getEmail")
    public ResponseEntity<String> findEmailAddress(@RequestParam("email") String email){
        HttpStatus status = null;
        String emailAddress = null;
        try{
             emailAddress  = service.findEmailAddress(email);
            if(emailAddress != null){
               status = HttpStatus.OK;
            }

        }catch (Exception ex){
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<String>(emailAddress, status);
    }
}
