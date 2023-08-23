package com.example.basketo.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.repository.UserInfoRepository;
import com.example.basketo.shopadmin.user.service.OtpService;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	UserInfoRepository userRepository;
	
	@Autowired
	OtpService otpService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

	
		  String redirectUrl = "";

         
		 // Check if the user has the "ROLE_ADMIN" authority

	    if (authentication.getAuthorities().stream()

	            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {

	        redirectUrl = "/adminpanel";
	        
	        System.err.println("redirect to /adminpanel");

	      
	    } else {
	    	
	    	 UserDetails userDetails = (UserDetails) authentication.getPrincipal();
 	         String username = userDetails.getUsername();
 	         Optional<UserInfo> customer = userRepository.findByUsername(username);
 	         
 	         String email =  customer.get().getEmail();
 	         
 	         otpService.sendOtp(email);
 	         
 	        System.err.println("redirect to /login/otpVerification");
        
 	        redirectUrl = "/login/otpVerification";

	    }
	    	System.out.println(redirectUrl+" kjaskhask");
	    
 	    	
 	        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);

 	    
		
	}
}
