package com.example.basketo.shopclient.controller;


import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.basketo.shopclient.service.ProfileServiceImpl;
import com.example.basketo.shopadmin.user.dto.CreateUserRequest;
import com.example.basketo.shopadmin.user.model.Role;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.repository.RoleRepository;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;

import lombok.RequiredArgsConstructor;
@Controller
@RequestMapping("/your-account")
@RequiredArgsConstructor
public class ProfileController {
	//display user profile page
	
	private final UserInfoService userInfoService;
	
	private final ProfileServiceImpl profileService;
	
	private final UsernameProviderService usernameProviderService;
	
	private final PasswordEncoder encoder;
	
	private final RoleRepository roleRepository;
	
	
	
    @GetMapping
    public String viewProfile(Model model) {
    	
//    	    model.addAttribute("oldPassword",)
    	    model.addAttribute("user",new CreateUserRequest());
    
    	return "front-end/your-account";

    }

    
     @PostMapping
	 public String saveProfile(@ModelAttribute("UserInfo ") UserInfo userInfo) {
		
		return "redirect:/your-account";
	}

    
     @PostMapping("/verify")
     public String verifyUserForEditingAccount(@ModelAttribute("password") String password, Model model ) {
    	 
    	 
    	
    	 
    	    UserInfo user =  userInfoService.getUser(usernameProviderService.get());
    	    List<Role> roles = roleRepository.findAll();
    	    System.err.println(user.getPassword());
    	    System.out.println(encoder.encode(password));
    	    
    	    if (encoder.matches(password,user.getPassword())) {
    	    	
    	    	System.err.println("password matches.");
    	    	model.addAttribute("user",user);
    	    	model.addAttribute("roles",roles);
				return "front-end/account&security";
			}
    	    
    	 return "redirect:/your-account";
    	 
		
    	 
     }
     
     @PostMapping("/account")
     public String editProfileAndAuthentication(@ModelAttribute("user") UserInfo user,  Model model) {
	    	
	    	System.out.println("***********");
	    	System.err.println("inside /account");
//	         System.out.println(selectedRole.getRoleName());
//	    	
	    	
	        UUID id = user.getUuid();
//	        user.setRole(selectedRole);

	        int result = profileService.updateAccount(id, user);

	        if (result > 0) {
	            switch (result) {
	                case 1:
	                    model.addAttribute("nameerror", "User with provided username already exists.");
	                    break;
	                case 2:
	                    model.addAttribute("emailerror", "User with provided email already exists.");
	                    break;
	                case 3:
	                    model.addAttribute("phoneerror", "User with provided phone number already exists.");
	                    break;
	            }

//	            List<Role> roles = roleRepository.findAll();
//	            model.addAttribute("roles", roles);
	            return "front-end/account&security";
	        }

	        model.addAttribute("successMessage", "Account details updated successfully.");
	        return "front-end/account&security";
	    }

     
     @PostMapping("/security")
     public String editAuthentication(@ModelAttribute("selectedRole") String roleName,Model model) {
    	 System.err.println("hi");
    	  System.out.println(roleName);
    	  model.addAttribute("securityUpdate","saved authentication method.");
    	  
    	  return "front-end/account&security";
		   
		}
     
     

    
    
}
	

