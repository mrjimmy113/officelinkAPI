package com.fpt.officelink.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
import com.fpt.officelink.dto.DepartmentDTO;
import com.fpt.officelink.dto.LocationDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.dto.PasswordInfoDTO;
import com.fpt.officelink.dto.ResetAccountDTO;
import com.fpt.officelink.dto.TeamDTO;
import com.fpt.officelink.dto.WorkplaceDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.mail.service.MailService;
import com.fpt.officelink.service.AccountService;
import com.fpt.officelink.service.JwtService;
import com.fpt.officelink.service.LocationService;

@RestController
@RequestMapping("/account")
public class AccountController {

    private CustomUser user;
    
    
    @Value("${angular.path}")
    private String angularPath;

    private CustomUser getUserContext() {
        return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Autowired
    AccountService service;



    @Autowired
    MailService mailService;


    @Autowired
    JwtService jwt;

    @Autowired
    LocationService locationService;


    //search with term
    @Secured({"ROLE_employer","ROLE_system_admin"})
    @GetMapping
    public ResponseEntity<PageSearchDTO<AccountDTO>> searchWithTerm(@RequestParam("term") String term){
        this.user = getUserContext();
        HttpStatus status = null;

        PageSearchDTO<AccountDTO> pageSearchDTO = new PageSearchDTO<>();

        try{
            Page<Account>  pageAccount = service.searchWithPagination(term, user.getWorkplaceId(),2, 0);

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


    //confirm-register, get info of employer
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

    //get profile
    @Secured({"ROLE_employer","ROLE_employee","ROLE_manager","ROLE_system_admin"})
    @GetMapping(value = "/profile")
    public ResponseEntity<AccountDTO> getProfile(){
        CustomUser user = getUserContext();
        HttpStatus httpStatus = null;
        AccountDTO accountDTO = new AccountDTO();



        Account entity = null;
        try{

             entity = service.getProfile(user.getUsername());
             BeanUtils.copyProperties(entity, accountDTO);

            httpStatus = HttpStatus.OK;


        }catch (Exception ex){
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<AccountDTO>(accountDTO, httpStatus);
    }



    //get account assigned
    @Secured({"ROLE_employer","ROLE_system_admin"})
    @GetMapping(value = "/getAccountAssign")
    public ResponseEntity<AccountDTO> getAccountAssign(@RequestParam("id") Integer id){
        CustomUser user = getUserContext();
        HttpStatus httpStatus = null;
        AccountDTO dto = new AccountDTO();

        Account account = null;
        try{

            account = service.getAccountAssign(id);
            LocationDTO locationDTO = new LocationDTO();
            List<TeamDTO> teamDTOS = new ArrayList<TeamDTO>();
            account.getTeams().forEach(element -> {
                TeamDTO teamDTO = new TeamDTO();
                DepartmentDTO depDTO = new DepartmentDTO();
                BeanUtils.copyProperties(element, teamDTO);
                BeanUtils.copyProperties(element.getDepartment(), depDTO);
                teamDTO.setDepartment(depDTO);
                teamDTOS.add(teamDTO);
            });

            if(account.getLocation() != null) {
            	BeanUtils.copyProperties(account.getLocation(), locationDTO);
            }
            
            BeanUtils.copyProperties(account, dto);

            dto.setLocation(locationDTO);
            dto.setTeams(teamDTOS);

            httpStatus = HttpStatus.OK;

        }catch (Exception ex){
        	ex.printStackTrace();
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<AccountDTO>(dto, httpStatus);
    }




    @GetMapping(value = "/getAccountByEmail")
    public ResponseEntity<AccountDTO> getAccountByEmail(@RequestParam("emailToken") String emailToken){
        HttpStatus status = null;
        Account account = null;
        AccountDTO accountDTO = new AccountDTO();

        try{
            String email = jwt.getEmailFromToken(emailToken);

            account = service.getAccountByEmail(email).get();
            BeanUtils.copyProperties(account, accountDTO);
            status = HttpStatus.OK;

        }catch (Exception ex){
            status = HttpStatus.BAD_REQUEST;

        }

        return new ResponseEntity<AccountDTO>(accountDTO, status);
    }



    //search get page
    @Secured({"ROLE_employer","ROLE_system_admin"})
    @GetMapping(value = "/getAccount")
    public ResponseEntity<PageSearchDTO<AccountDTO>> searchGetPage(@RequestParam("term") String term, @RequestParam("page") int page){
        this.user = getUserContext();
        HttpStatus status = null;

        PageSearchDTO<AccountDTO> pageSearchDTO = new PageSearchDTO<>();

        try{
            Page<Account>  pageAccount = service.searchWithPagination(term, user.getWorkplaceId(), 2,  page);

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

    //search get page with account not assign
    @Secured({"ROLE_employer","ROLE_system_admin"})
    @GetMapping(value = "/getAccountNotAssign")
    public ResponseEntity<PageSearchDTO<AccountDTO>> searchAccountNotAssgin(@RequestParam("term") String term, @RequestParam("page") int page){
        this.user = getUserContext();
        HttpStatus status = null;

        PageSearchDTO<AccountDTO> pageSearchDTO = new PageSearchDTO<>();

        try{
            Page<Account>  pageAccount = service.searchAccountNotAssign(term, user.getWorkplaceId(), 2,  page);

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




    @PostMapping(value = "/createAccount")
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




    //send Mail to Register
    @PostMapping(value = "/sendMailRegister")
    public ResponseEntity<Number> sendMailRegister(@RequestBody AccountDTO dto){
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
                model.put("link", angularPath+"/confirm/" + token);

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

    //send mail to reset password
    @PostMapping(value = "/sendMailReset")
    public ResponseEntity<Number> sendMailResetPassword(@RequestBody String email){
        HttpStatus status = null;
        AccountDTO dto = new AccountDTO();
        String token = null;
        Map<String, Object> model = new HashMap<>();
        List<String> listEmail = new ArrayList<>();
        try{
            boolean res = service.checkAccountExisted(email);
            if(res == false){
                listEmail.add(email);
                token =  jwt.createTokenWithEmail(email);
                service.sendMailResetPassword(listEmail, token);
                status = HttpStatus.OK;
            }
            if(res){
                status = HttpStatus.NOT_FOUND;
            }

        }catch (Exception ex){
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Number>(status.value(), status);
    }


    //update isActived after click confirm
    @PutMapping(value = "/confirm")
    public ResponseEntity<Number> createAccountByToken(@RequestBody String accountToken ){
        HttpStatus status = null;
        try{
            AccountDTO accountDTO = jwt.getAccountByToken(accountToken);

            Account entity = new Account();
            BeanUtils.copyProperties(accountDTO,entity);
            boolean res = service.updateIsActive(entity);

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


    // update info when employee join
    @PutMapping(value = "/updateEmployee")
    public ResponseEntity<Number> updateEmployee(@RequestBody AccountDTO dto ){
        HttpStatus status = null;
        try{


            Account entity = new Account();
            BeanUtils.copyProperties(dto,entity);
            boolean res = service.updateIsActive(entity);

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



    //update account
    @Secured({"ROLE_employer","ROLE_system_admin"})
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



    //send invite to employee
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


    //delete account
    @Secured({"ROLE_employer","ROLE_system_admin"})
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




    //get info of email invite
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



    //register when invite to employee
    @PostMapping("/acceptInvite")
    public ResponseEntity<Number> acceptInvite(@RequestBody String[] emailTo) {
        CustomUser user = getUserContext();
    	HttpStatus status = null;
    	try {
    	    for(int i = 0 ; i < emailTo.length ; i++){
                boolean res = service.checkAccountExisted(emailTo[i]);
                if(res){
                    service.acceptInvite(emailTo[i], 2 , user.getWorkplaceId());
                    status = HttpStatus.OK;
                }else{
                    status = HttpStatus.CONFLICT;
                }
            }

		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

    	return new ResponseEntity<Number>(status.value(),status);
    }

    @Secured({"ROLE_employer","ROLE_system_admin"})
    @PostMapping("/checkEmailExisted")
    public ResponseEntity<Number> checkEmailExisted(@RequestBody String email) {
        CustomUser user = getUserContext();
        HttpStatus status = null;
        try {

                boolean res = service.checkAccountExisted(email);
                if(res){

                    status = HttpStatus.OK;
                }else{
                    status = HttpStatus.CONFLICT;
                }


        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<Number>(status.value(),status);
    }



    //add assign
    @Secured({"ROLE_employer","ROLE_system_admin"})
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


    //reset password, change new password of forget password
    @PutMapping(value = "/resetPassword")
    public ResponseEntity<Number> ResetPassword(@RequestBody ResetAccountDTO resetAccountDTO) {
        HttpStatus status = null;
        String email = null;
        try {
            email = jwt.getEmailFromToken(resetAccountDTO.getEmailToken());
            service.resetPassword(email, resetAccountDTO.getNewPassword());
            status = HttpStatus.OK;
        } catch (Exception e) {
            e.printStackTrace();
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<Number>(status.value(),status);
    }


    //change profile
    @Secured({"ROLE_employer","ROLE_employee","ROLE_manager","ROLE_system_admin"})
    @PutMapping("/changeProfile")
    public ResponseEntity<Number> changeProfile(@RequestBody AccountDTO dto){
        HttpStatus status = null;
        Account account = new Account();
        try{
            BeanUtils.copyProperties(dto, account);
            boolean res = service.changeProfile(account);
            if(res){
                status = HttpStatus.OK;
            }

        }catch (Exception ex){
                status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Number>(status.value(), status);
    }


    //change password
    @Secured({"ROLE_employer","ROLE_employee","ROLE_manager","ROLE_system_admin"})
    @PutMapping("/changePassword")
    public ResponseEntity<Number> changePassword(@RequestBody PasswordInfoDTO dto){
        HttpStatus status = null;

        try{

            boolean res = service.changePassword(dto.getEmail(), dto.getCurrentPassword(), dto.getNewPassword() );
            if(res){
                status = HttpStatus.OK;
            }else {
                status = HttpStatus.BAD_REQUEST;
            }

        }catch (Exception ex){
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<Number>(status.value(), status);
    }



}
