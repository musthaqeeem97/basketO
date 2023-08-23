package com.example.basketo.shopadmin.user.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class UsernameProviderService {

	 @Autowired
	    UserInfoService userInfoService;
	    public UUID get() {
	        String username = SecurityContextHolder.getContext().getAuthentication().getName();
	        return userInfoService.findByUsername(username).get().getUuid();
	    }
}
