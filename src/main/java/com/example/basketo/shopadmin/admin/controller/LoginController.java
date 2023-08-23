package com.example.basketo.shopadmin.admin.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.repository.UserInfoRepository;
import com.example.basketo.shopadmin.user.service.OtpService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {

	private final UserInfoRepository userRepository;
	private final OtpService otpService;
	
	@GetMapping("/login")
	public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {

	
    	/*
    	SecurityContextHolder.getContext().getAuthentication() retrieves the 
    	currently authenticated user's Authentication object from the security context. 
    	
    	*/	 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
        	
        
        	
        	
        	if (error != null) {
    	        model.addAttribute("error", "Invalid username or password");
    	    }
    		
            return "login";
        }
//        else if (authentication != null && authentication.isAuthenticated()) {
// 		   
//		    List<String> roleNames = authentication.getAuthorities()
//		    	    .stream()
//		    	    .map(GrantedAuthority::getAuthority)
//		    	    .collect(Collectors.toList());
//		    if (!roleNames.contains("ROLE_VERIFIED_USER")) {
//		    	
//		    	SecurityContextHolder.clearContext();
//		    	
//				return "login";
//			}
//		}
        
        

        return "redirect:/";
    }
	
  

	
	
	
	@GetMapping("/login/otpVerification")
    public String login( HttpSession session, Model model) {
		
		System.err.println("inside login/otpVerification ");
        // Validate the email and password against the database or any other authentication mechanism
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority>  authourities  = authentication.getAuthorities();
		
		org.springframework.security.core.userdetails.User authenticatedUser = 
				(org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        
            // Create a new User object for the authenticated user
           
            session.setAttribute("user", authenticatedUser);

            // Redirect to the OTP screen for verification
            return "otpScreen";
    
    }

   

	
	@PostMapping("/verifyOtp")
    public String verifyOtp(@RequestParam("otp") String otp, HttpSession session, Model model) {
        // Use the 'otp' parameter to access the OTP input value
        System.err.println("OTP Input: " + otp);
        System.err.println("session: " + session);
        System.err.println("session.getAttribute(\"user\"): " + session.getAttribute("user"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        String username = authentication.getName();
        
        System.err.println("autmehenticated user name: " + username);

       Optional<UserInfo> optionalUser = userRepository.findByUsername(username);
       UserInfo authenticatedUser = optionalUser.get();
        
     
        
        // Verify the OTP in the database
       

        if (otpService.verifyOtp(authenticatedUser.getEmail(), otp)) {
            // If OTP is valid, update the user's authority to ROLE_VERIFIED_USER

            // Update the user's authority to ROLE_VERIFIED_USER
            List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
            updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_VERIFIED_USER"));
            Authentication updatedAuthentication = new UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(), authentication.getCredentials(), updatedAuthorities);

           
            // Authenticate the updated authentication token
           // Authentication updatedAuthenticationResult = authenticationManager.authenticate(updatedAuthentication);

            // Update the SecurityContext with the new authentication token
            SecurityContextHolder.getContext().setAuthentication(updatedAuthentication);

            // Redirect to the home page or any other page
            return "redirect:/";
        } else {
            // If OTP is invalid, show an error message
            model.addAttribute("error", "Invalid OTP");

            // Return the OTP verification page again
            return "otpScreen";
        }
    
    }
	
}

	