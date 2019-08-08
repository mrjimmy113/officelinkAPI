package com.fpt.officelink.service;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import com.fpt.officelink.entity.Location;
import com.fpt.officelink.entity.Workplace;
import com.nimbusds.jose.JOSEException;

import org.springframework.data.domain.Page;

import com.fpt.officelink.dto.AccountDTO;
import com.fpt.officelink.dto.AuthDTO;
import com.fpt.officelink.entity.Account;

public interface AccountService {

    Page<Account> searchWithPagination(String term, Integer workplaceId , Integer roleId , int pageNum);
    boolean addNewAccount(Account account, Integer roleId ,  String workplaceName);
    boolean modifyAccount(Account account , Integer roleId , Location address , Workplace workplace);
    boolean updateIsActive(Account account);

    void removeAccount(int id);
    boolean checkAccountExisted(String email);
    Optional<Account> getAccountByEmail(String email);
	AuthDTO getAuthenticationInfor(String email, String password);

	List<Account> findAccountByWorkplaceId(Integer id);
	void sendInvitation(String[] listEmail) throws JOSEException, ParseException;
	AccountDTO getInvitationInfor(String token) throws ParseException;
	void acceptInvite(String email, Integer roleId, Integer workplaceId);
	void assignMember(int locationId, int[] teamIdList, int accountId);


	Account getProfile(String email);

	Account getAccountAssign( Integer id);

	boolean changeProfile(Account account );

	boolean changePassword(String email, String currentPass, String newPass);
	void sendMailResetPassword(List<String> listEmail , String accountToken);

	void resetPassword(String email ,String newPassword);

	void unassignedFromTeam(int teamId, int accountId, int workplaceId);



}
