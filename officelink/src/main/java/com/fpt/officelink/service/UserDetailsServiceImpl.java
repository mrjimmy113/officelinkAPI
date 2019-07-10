package com.fpt.officelink.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Account;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.repository.AccountRespository;

@Service("authService")
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	AccountRespository rep;
	
	private List<GrantedAuthority> buildRoles(String role) {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_" + role));
        return roles;
    }

    private UserDetails userWithRoles(Account acc, List<GrantedAuthority> roles) {
        return new CustomUser(
               acc.getEmail(), acc.getPassword(), true, true, true, true, roles, acc.getWorkplace().getId());
    }
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Account> acc = rep.findByEmail(username);
		if(acc.isPresent()) {
			List<GrantedAuthority> roles = buildRoles(acc.get().getRole().getRole());
	        return userWithRoles(acc.get(), roles);
		}
		return null;
	}
}
