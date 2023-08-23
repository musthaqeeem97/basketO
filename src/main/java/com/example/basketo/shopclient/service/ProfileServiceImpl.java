package com.example.basketo.shopclient.service;

import com.example.basketo.shopadmin.user.model.Role;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.repository.RoleRepository;
import com.example.basketo.shopadmin.user.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl {
	
	private final UserInfoRepository userInfoRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder encoder;

	 public Optional<UserInfo> findByUsername(String username){	    	
	    	return userInfoRepository.findByUsername(username);
	    }
	    
	    private Optional<UserInfo> findByEmail(String email) {	
			return userInfoRepository.findByEmail(email);
		}
	    
	    public Optional<UserInfo> findByUuid(UUID uuid){
	    	return userInfoRepository.findById(uuid);
	    }

	  
	    public UserInfo findByPhone(String phone) {
	        return userInfoRepository.findByPhone(phone);
	    }
	public int updateAccount(UUID uuid, UserInfo updatedUser) {
		
		UserInfo userByEmail = findByEmail(updatedUser.getEmail()).orElse(null);
		UserInfo userByPhone = findByPhone(updatedUser.getPhone());
		
		//user details in db
		UserInfo userDb = findByUuid(uuid).get();
		
		
	     if (userByEmail!=null && !updatedUser.getEmail().equals(userDb.getEmail())) {
			return 1;//email exists
		}
		else if (userByPhone!=null && !updatedUser.getPhone().equals(userDb.getPhone())) {
			return 2;//phone number exists
	}
		   System.err.println("all two checks done");
		   if(updatedUser.getPassword().isEmpty()){
	            updatedUser.setPassword(userDb.getPassword());
	        }
	        else{
	            updatedUser.setPassword(encoder.encode(updatedUser.getPassword()));
	        }
		   
		   	Optional<Role> userRole = roleRepository.findRoleByName("ROLE_USER");
	        Role role_user = userRole.get();
	        updatedUser.setRole(role_user);
	        
		   userInfoRepository.save(updatedUser);
	        
	       
				
		return 0;
		
	}
	

}
