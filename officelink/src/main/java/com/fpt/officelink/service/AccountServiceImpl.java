package com.fpt.officelink.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import com.fpt.officelink.entity.Location;
import com.fpt.officelink.entity.Role;
import com.fpt.officelink.entity.Team;
import com.fpt.officelink.repository.LocationRepository;
import com.fpt.officelink.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fpt.officelink.dto.AccountDTO;
import com.fpt.officelink.dto.AuthDTO;
import com.fpt.officelink.dto.WorkplaceDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.mail.service.MailService;
import com.fpt.officelink.repository.AccountRespository;
import com.fpt.officelink.repository.WorkplaceRepository;
import com.nimbusds.jose.JOSEException;

@Service
public class AccountServiceImpl implements AccountService {

    private static final int PAGEMAXSIZE = 9;
    
    @Value("${angular.path}")
	private String angularPath;

    @Autowired
    AccountRespository accountRespository;
    
    @Autowired
    WorkplaceService workplaceService; 
    
    @Autowired
    WorkplaceRepository workplaceRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    JwtService jwtSer;
    
    @Autowired
    MailService mailService;


    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    private CustomUser getUserContext() {
    	return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
   



    @Override
    public Page<Account> searchWithPagination(String term, Integer workplaceId, Integer roleId ,  int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, PAGEMAXSIZE);
        //return accountRespository.find(term, workplaceId, false , pageable);
        return accountRespository.findAllByFirstnameAndWorkplaceAndRole(term, workplaceId, false , roleId , pageable);
    }

    @Transactional
    @Override
    public boolean addNewAccount(Account account, Integer roleId , String workplaceName) {
        Optional<Account> acc = accountRespository.findAccountByEmail( account.getEmail());
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(acc.isPresent()){
            return false;
        }else{
        	// create workplace
        	Workplace workplace = new Workplace();
        	workplace.setName(workplaceName);
        	workplace.setDateCreated(new Date());
        	workplaceRepository.save(workplace);




        	// create account with the new workplace
        	account.setWorkplace(workplace);

            account.setRole(optionalRole.get());
            account.setPassword(passwordEncoder().encode(account.getPassword()));
            account.setDateCreated(new Date());
            accountRespository.save(account);
            return true;
        }


    }




    @Override
    public boolean modifyAccount(Account account, Integer roleId , Location location , Workplace workplace) {
        Optional<Account> optionalAccount = accountRespository.findById(account.getId());
        Optional<Role> optionalRole = roleRepository.findById(roleId);

        if(optionalAccount.isPresent()){

            location.setAddress(location.getAddress());
            location.setDateModified(new Date());
            locationRepository.save(location);

            workplace.setName(workplace.getName());
            workplaceRepository.save(workplace);

            account.setRole(optionalRole.get());
            account.setDateModified(new Date());
            account.setWorkplace(workplace);
            account.setLocation(location);
            accountRespository.save(account);
            return true;
        }

        return false;

    }

    @Override
    public void removeAccount(int id) {
        Account account = accountRespository.findById(id).get();
        if(account != null){
            account.setDeleted(true);
        }
        accountRespository.save(account);
    }

    @Override
    public Optional<Account> getAccountByEmail(String email) {
        Optional<Account> account = accountRespository.findByEmail(email);
        return account;
    }
    
    @Override
    public AuthDTO getAuthenticationInfor(String email, String password) {
    	AuthDTO result = null;
    	Optional<Account> acc = accountRespository.findByEmail(email);
    	if(acc.isPresent()) {
    		if(passwordEncoder().matches(password, acc.get().getPassword())) {
    			result = new AuthDTO();
        		result.setRole(acc.get().getRole().getRole());
        		result.setName(acc.get().getFirstname() + " " + acc.get().getLastname());
        		result.setToken(jwtSer.createTokenWithEmail(acc.get().getEmail()));
    		}
    		
    	}
    	return result;
    }

    @Override
    public List<Account> findAccountByWorkplaceId(Integer id) {
        return accountRespository.findAllByWorkplaceId(id, false);
    }

    @Override
    public boolean checkAccountExisted(String email) {
        Optional<Account> acc = accountRespository.findAccountByEmail( email);
        if(acc.isPresent()){
            return false;
        }
        return true;
    }
    
    @Override
    public void sendInvitation(String[] listEmail) throws JOSEException, ParseException {
    	List<String> tokenList = new ArrayList<String>();
    	Map<String, Object> model = new HashMap<>();
    	for (int i = 0; i < listEmail.length; i++) {
    		System.out.println(getUserContext().getWorkplaceId());
			tokenList.add(jwtSer.createInviteToken(listEmail[i], getUserContext().getWorkplaceId()));
		}
    	for (String token : tokenList) {
    		model.put("link", angularPath + "/join/" + token);
    		String[] mail = new String[1];
    		mail[0] = jwtSer.getEmailFromToken(token);
    		System.out.println(jwtSer.getWorkplaceId(token));
    		mailService.sendMail(mail,"email-invite-temp.ftl", model);
		}
    	
    }

    @Override
    public void sendMailResetPassword(List<String> listEmail , String token) {
        Map<String, Object> model = new HashMap<>();
        model.put("link", "http://localhost:4200/change-password/" + token);
        mailService.sendMail(listEmail.toArray(new String[listEmail.size()]),"reset-password-temp.ftl", model );

    }

    @Override
    public void resetPassword(String email , String newPassword) {
        Account account = accountRespository.findAllByEmail(email);
        account.setPassword(passwordEncoder().encode(newPassword));
        accountRespository.save(account);
    }


    @Override
    public AccountDTO getInvitationInfor(String token) throws ParseException {
    	System.out.println("Hello");
    	AccountDTO dto = new AccountDTO();
    	dto.setEmail(jwtSer.getEmailFromToken(token));
    	WorkplaceDTO workplaceDTO = new WorkplaceDTO();
    	workplaceDTO.setId(jwtSer.getWorkplaceId(token));
    	dto.setWorkplace(workplaceDTO);    	
    	return dto;
    }
    
    @Override
    public void acceptInvite(Account entity, Integer roleId, Integer workplaceId) {
    	Role role = new Role();
    	role.setId(roleId);
    	Workplace workplace = new Workplace();
    	workplace.setId(workplaceId);
    	entity.setWorkplace(workplace);
    	entity.setRole(role);
    	entity.setPassword(passwordEncoder().encode(entity.getPassword()));
    	accountRespository.save(entity);
    	
    }
    
    @Override
    public void assignMember(int locationId, int[] teamIdList, int accountId) {
    	Location location = new Location();
    	location.setId(locationId);
    	List<Team> teams = new ArrayList<Team>();
    	for (int i = 0; i < teamIdList.length; i++) {
			Team team = new Team();
			team.setId(teamIdList[i]);
			teams.add(team);
		}
    	Optional<Account> opAc = accountRespository.findById(accountId);
    	if(opAc.isPresent()) {
    		Account acc = opAc.get();
    		acc.setLocation(location);
    		acc.setTeams(new HashSet<Team>(teams));
    		accountRespository.save(acc);
    	}
    }

    @Override
    public Account getProfile(String email) {
        Account account = null;
        account = accountRespository.findAllByEmail(email);
        return account;
    }

    @Override
    public Account getAccountAssign(Integer id) {
        Account account = accountRespository.findById(id).get();
        if(account != null){

        }

        return account;
    }

    @Override
    public boolean changeProfile(Account account) {
        Optional<Account> optionalAccount = accountRespository.findByEmail(account.getEmail());
        if(optionalAccount.isPresent()){
            optionalAccount.get().setFirstname(account.getFirstname());
            optionalAccount.get().setLastname(account.getLastname());
            optionalAccount.get().setAddress(account.getAddress());
            accountRespository.save(optionalAccount.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean changePassword(String email, String currentPass, String newPass) {
        Optional<Account> optionalAccount = accountRespository.findByEmail(email);
        if(optionalAccount.isPresent()){
            if(passwordEncoder().matches(currentPass, optionalAccount.get().getPassword())){
                optionalAccount.get().setPassword(passwordEncoder().encode(newPass));
                accountRespository.save(optionalAccount.get());
                return true;
            }
        }
        return false;
    }



}
