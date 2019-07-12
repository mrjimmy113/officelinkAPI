package com.fpt.officelink.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpt.officelink.dto.AccountDTO;
import com.fpt.officelink.dto.AssignInforDTO;
import com.fpt.officelink.dto.LocationDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.dto.WorkplaceDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.mail.service.MailService;
import com.fpt.officelink.service.AccountService;
import com.fpt.officelink.service.JwtService;

@RestController
@RequestMapping("/account")
public class AccountController {

    private CustomUser user;

    private CustomUser getUserContext() {
        return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Autowired
    AccountService service;



    @Autowired
    MailService mailService;


    @Autowired
    JwtService jwt;


    @GetMapping
    public ResponseEntity<PageSearchDTO<AccountDTO>> searchWithTerm(@RequestParam("term") String term){
        this.user = getUserContext();
        HttpStatus status = null;

        PageSearchDTO<AccountDTO> pageSearchDTO = new PageSearchDTO<>();

        try{
            Page<Account>  pageAccount = service.searchWithPagination(term, user.getWorkplaceId(), 0);

            List<AccountDTO> listAccount = new ArrayList<AccountDTO>();


            pageAccount.getContent().forEach(element -> {
                AccountDTO accountDTO = new AccountDTO();
                WorkplaceDTO workplaceDTO = new WorkplaceDTO();
                LocationDTO locationDTO = new LocationDTO();

                BeanUtils.copyProperties(element.getWorkplace(), workplaceDTO);
                BeanUtils.copyProperties(element,accountDTO);

                accountDTO.setLocation(locationDTO);
                accountDTO.setWorkplace(workplaceDTO);

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

    @GetMapping(value = "/confirm")
    public ResponseEntity<AccountDTO> getAccountByToken(@RequestParam("accountToken") String accountToken){

        HttpStatus status = null;
        AccountDTO accountDTO = new AccountDTO();
        try{
            accountDTO = jwt.getAccountByToken(accountToken);
            status = HttpStatus.OK;

        }catch (Exception ex){
            status = HttpStatus.BAD_REQUEST;
            ex.printStackTrace();
        }


        return new ResponseEntity<AccountDTO>(accountDTO, status);

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
        this.user = getUserContext();
        HttpStatus status = null;

        PageSearchDTO<AccountDTO> pageSearchDTO = new PageSearchDTO<>();

        try{
            Page<Account>  pageAccount = service.searchWithPagination(term, user.getWorkplaceId(), page);

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
            boolean res = service.addNewAccount(entity, dto.getRole_id(), dto.getWorkplace().getName());
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




    @PostMapping(value = "/sendMail")
    public ResponseEntity<Number> sendMail(@RequestBody AccountDTO dto){
        HttpStatus status = null;
        Map<String, Object> model = new HashMap<>();



        try {

            boolean res = service.checkAccountExisted(dto.getEmail());
            if(res) {

                String token = null;
                List<String> listEmail = new ArrayList<>();

                String emailTo = dto.getEmail();

                listEmail.add(emailTo);
                Integer role_id = dto.getRole_id();

                token = jwt.createTokenWithAccount(dto);
                model.put("link", "http://localhost:4200/confirm/" + token);

                mailService.sendMail(listEmail.toArray(new String[listEmail.size()]),"email-temp.ftl", model);

                status = HttpStatus.OK;
            } else{
                status = HttpStatus.CONFLICT;
            }
        }catch (Exception ex){
            status = HttpStatus.BAD_REQUEST;
        }


        return new ResponseEntity<Number>(status.value(), status);
    }


    @PostMapping(value = "/confirm")
    public ResponseEntity<Number> createAccountByToken(@RequestBody String accountToken ){
        HttpStatus status = null;
        try{
            AccountDTO accountDTO = jwt.getAccountByToken(accountToken);

            Account entity = new Account();
            BeanUtils.copyProperties(accountDTO,entity);
            boolean res = service.addNewAccount(entity, accountDTO.getRole_id(), accountDTO.getWorkplace().getName());


            if(res){

                status = HttpStatus.OK;
            }else {
                status = HttpStatus.CONFLICT;
            }

        }catch (Exception ex){
            status = HttpStatus.BAD_REQUEST;
            ex.printStackTrace();
        }


        return new ResponseEntity<Number>(status.value(), status);

    }


    @PutMapping
    public ResponseEntity<Integer> update(@RequestBody AccountDTO dto) {
        HttpStatus status = null;
        try {
            Account entity = new Account();
            Location location = new Location();
            Workplace workplace = new Workplace();
            BeanUtils.copyProperties(dto.getWorkplace(), workplace);
            BeanUtils.copyProperties(dto,entity);


            boolean res = service.modifyAccount(entity, dto.getRole_id(), location , workplace );


            if(res){
                status = HttpStatus.CREATED;
            }

        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
            e.printStackTrace();
        }

        return new ResponseEntity<Integer>(status.value(), status);
    }



    @PostMapping(value = "/sendInvite")
    public ResponseEntity<Number> createAccountByToken(@RequestBody String[] emailTo ){
        HttpStatus status = null;
            try{
                service.sendInvitation(emailTo);
                status = HttpStatus.OK;

        }catch (Exception ex){
            status = HttpStatus.BAD_REQUEST;
            ex.printStackTrace();
        }


        return new ResponseEntity<Number>(status.value(), status);

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
    
    @GetMapping("/invitationInfor")
    public ResponseEntity<AccountDTO> getInvitationInfor(@RequestParam("token") String token) {
    	AccountDTO res = new AccountDTO();
    	HttpStatus status = null;
    	try {
			res = service.getInvitationInfor(token);
    		status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
    	
    	return new ResponseEntity<AccountDTO>(res,status);
    }
    
    @PostMapping("/acceptInvite")
    public ResponseEntity<Number> acceptInvite(@RequestBody AccountDTO acc) {
    	HttpStatus status = null;
    	try {
			Account entity = new Account();
			BeanUtils.copyProperties(acc, entity);
			service.acceptInvite(entity, acc.getRole_id(), acc.getWorkplace().getId());
    		status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
    	
    	return new ResponseEntity<Number>(status.value(),status);
    }
    
    @PutMapping("/assign")
    public ResponseEntity<Number> assign(@RequestBody AssignInforDTO dto) {
    	HttpStatus status = null;
    	try {
			service.assignMember(dto.getLocationId(), dto.getTeamIdList(), dto.getAccountId());
    		status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
    	
    	return new ResponseEntity<Number>(status.value(),status);
    }




}
