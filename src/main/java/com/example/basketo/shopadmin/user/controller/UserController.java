package com.example.basketo.shopadmin.user.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.basketo.shopadmin.user.dto.CreateUserRequest;
import com.example.basketo.shopadmin.user.model.Role;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.repository.RoleRepository;
import com.example.basketo.shopadmin.user.repository.UserInfoRepository;
import com.example.basketo.shopadmin.user.service.UserInfoService;
import com.example.basketo.shopadmin.user.service.UsernameProviderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@RequestMapping("/adminpanel/users")
public class UserController {

	private final UserInfoService userInfoService;
	private final UsernameProviderService usernameProvider;
	private final RoleRepository roleRepository;
	private final UserInfoRepository userInfoRepository;
	
	@GetMapping
	   @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String users(Model model){
		
		return getUsersPaaginated(1, "username", "asc","", model);
	}
	@GetMapping("/page/{pageNo}")
	public String getUsersPaaginated(@PathVariable(value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField,@RequestParam("sortDir") String sortDir,
			@RequestParam("searchTerm") String SearchTerm,
			Model model) {
			
			Page<UserInfo> page = userInfoService.findPaginated(pageNo, 2, sortField, sortDir,SearchTerm);
			
			List<UserInfo> listUserInfo = page.getContent();
			
			model.addAttribute("currentPage", pageNo);
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("totalItems", page.getTotalElements());
			
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortDir", sortDir);
			model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
			model.addAttribute("searchTerm",SearchTerm);
			
			model.addAttribute("listUserInfo", listUserInfo);
		
		return "adminpanel-users";
		
	}
	 
	    
	    @GetMapping("/create")
	    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	    public String create(Model model) {
		
	    	System.out.println("***********");
	    	System.err.println("inside /create");
	    	model.addAttribute("user",new CreateUserRequest());
	    	return "create-user";
	    }
	 
		@PostMapping("/create")
		public String createUser(@ModelAttribute("user") CreateUserRequest createUserRequest, Model model) {
			
			if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
				 
				return "create-user";
			}
			
		    int result = userInfoService.createUser(createUserRequest);

		    if (result == 1|| result==2 || result==3 ) {
		        if (result == 1) {
		            model.addAttribute("nameerror", "The username already exists");
		        } else if(result==2 ) {
		            model.addAttribute("emailerror", "The email ID already exists");         
		        }else {
		        	model.addAttribute("phoneerror", "The phone number already exists.");
		        }
		        
		        return "create-user";
		    }
		  
		    	 return "redirect:/adminpanel/users";    	
		   
		}
		
	

		@GetMapping("/edit/{id}")
		@PreAuthorize("hasAuthority('ROLE_ADMIN')")
		public String edit(@PathVariable("id") UUID id ,Model model) {
			System.out.println("***********");
	    	System.err.println("inside /edit");
			Optional<UserInfo> user = userInfoService.findByUuid(id);
			
			UserInfo userToEdit = user.get() ;
			System.out.println(user.get().getUsername());
			
			List<Role> role = roleRepository.findAll();
			role.stream()
            .map(Role::getRoleName)
            .forEach(System.out::println);
			
			model.addAttribute("user", userToEdit);
			model.addAttribute("roles", role);
			
			System.err.println("inside /edit end ");
			return "update-user";
			
		}


		    @PostMapping("/update")
		    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
		    public String updateUser(@ModelAttribute("user") UserInfo user, @RequestParam("selectedRole") Role selectedRole, Model model) {
		    	
		    	System.out.println("***********");
		    	System.err.println("inside /update");
		    	System.out.println(selectedRole.getRoleName());
		    	
		    	
		        UUID id = user.getUuid();
		        user.setRole(selectedRole);

		        int result = userInfoService.updateUser(id, user);

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

		            List<Role> roles = roleRepository.findAll();
		            model.addAttribute("roles", roles);
		            return "update-user";
		        }

		        model.addAttribute("success", "User updated successfully.");
		        return "redirect:/adminpanel/users";
		    }


		    @GetMapping("/active/{id}")
		    public String toggleUserStatus(@PathVariable("id") UUID id) {
		        Optional<UserInfo> optionalUserInfo = userInfoService.findByUuid(id);
		        
		        if (optionalUserInfo.isPresent()) {
		        	
		            UserInfo userInfo = optionalUserInfo.get();
		            
		          //if enabled == true will change it to false if false will change it true
		            Boolean status = !userInfo.isEnabled();
		            userInfo.setEnabled(status);
		            
		            if (userInfo.isEnabled()==false) {
		            	userInfo.setDeleted(true);
		            	userInfo.setDeletedAt(new Date());
		            	userInfo.setDeletedBy(usernameProvider.get());
					}else {
						userInfo.setDeleted(false);
						userInfo.setCreatedAt(new Date());
					}
		            userInfoRepository.save(userInfo);
		        }
		        return "redirect:/adminpanel/users";
		    }


		    //get current username
		    public String getCurrentUsername() {
		    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    	return authentication.getName();
		    }
			    		
			
		    @GetMapping("/delete/{id}")
		    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
		    public String delete(@PathVariable("id") UUID id ) {
		    	
	            userInfoService.deleteById(id);
		    	System.out.println(" deleted the user having id " + id);
		    	
		    	return "redirect:/adminpanel/users";
		    }
		 

}
