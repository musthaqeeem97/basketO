package com.example.basketo.shopadmin.admin.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.basketo.shopadmin.user.dto.CreateUserRequest;
import com.example.basketo.shopadmin.user.dto.SignUpRequest;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.repository.UserInfoRepository;
import com.example.basketo.shopadmin.user.service.OtpService;
import com.example.basketo.shopadmin.user.service.UserInfoService;


import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignupController {

	private final UserInfoService userService;
	private final UserInfoRepository userRepository;
	private final OtpService otpService;
	
	@GetMapping
	public String getSignUpPage(Model model) {
		
		model.addAttribute("signuprequest",new SignUpRequest());
		
		return "signup";
	}

	
	@PostMapping
	public String signup(@ModelAttribute("signuprequest") CreateUserRequest signUpRequest, Model model, HttpSession session) {
	    System.err.println("inside signup post 1 controller");
	    if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
	        model.addAttribute("signuperror", "Passwords do not match");
	        return "/signup";
	    }

	    int result = userService.createUser(signUpRequest);

	    if (result == 1 || result == 2) {
	        if (result == 1) {
	            model.addAttribute("nameerror", "The username already exists");
	        } else {
	            model.addAttribute("emailerror", "The email ID already exists");
	        }
	        return "/signup";
	    }

	    System.err.println("inside signup post 2 controller");
	    otpService.sendOtp(signUpRequest.getEmail());
	    
	    
        session.setAttribute("newCustomerEmail", signUpRequest.getEmail());
	  
	    return "signup-otp";
	}
	
	@PostMapping("/verifyOtp")
	public String verifyNewCustomer(@RequestParam("otp") String otp, HttpSession session,Model model) {
	    System.err.println("INSIDE /signup/verifyOtp");
	    
	    String email = (String) session.getAttribute("newCustomerEmail");
	    System.err.println(email);
	    System.err.println(otp);
	    Optional<UserInfo> user = userRepository.findByEmail(email);
	    if (user.get().getOtp().equals(otp)) {
			user.get().setRegistered(true);
			model.addAttribute("userRegistered","customer registered.");
			 return "login";
		}
	    model.addAttribute("otpError","otp does not match.");
	    return "signup-otp";
	}

	
}
