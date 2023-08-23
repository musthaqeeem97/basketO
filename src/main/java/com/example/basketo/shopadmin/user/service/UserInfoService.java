package com.example.basketo.shopadmin.user.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.basketo.shopadmin.user.dto.CreateUserRequest;
import com.example.basketo.shopadmin.user.model.Role;
import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.basketo.shopadmin.user.repository.UserInfoRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserInfoService {

	private final UserInfoRepository userInfoRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder encoder;
 
		public int createUser(CreateUserRequest createUserRequest) {
		
			UserInfo userByName = findByUsername(createUserRequest.getUsername()).orElse(null);
			UserInfo userByEmail = findByEmail(createUserRequest.getEmail()).orElse(null);
			UserInfo userByPhone = findByPhone(createUserRequest.getPhone());
			
		if (userByName!=null) {
			
			return 1;
		}else if (userByEmail!=null) {
			return 2;
		}else if (userByPhone!=null) {
			return 3;
		}
	
		 Optional<Role> userRole = roleRepository.findRoleByName("ROLE_USER");
	        Role role = userRole.get();
	        
	       
	       
			UserInfo newUser = UserInfo.builder()
					.firstName(createUserRequest.getFirstName())
					.lastName(createUserRequest.getLastName())
					.phone(createUserRequest.getPhone())
				    .username(createUserRequest.getUsername())
				    .email(createUserRequest.getEmail())
				    .password(encoder.encode(createUserRequest.getPassword()))
				    .role(role)
				    .enabled(true)
				    .build();
		
		userInfoRepository.save(newUser);

		return 0;
		
	}
	
	 public List<UserInfo> loadAllUsers(){
	        return  userInfoRepository.findAll();
	    }

	    public Page<UserInfo> loadAllUsers(Pageable pageable){
	        return  userInfoRepository.findAll(pageable);
	    }

	    //findById
	    public UserInfo getUser(UUID id){
	        return userInfoRepository.findById(id).orElse(null); //new way
	     }
	     //save user
	    public void updateUser(UserInfo userInfo) { userInfoRepository.save(userInfo);  }

	    public UserInfo save(UserInfo userInfo){
	        return userInfoRepository.save(userInfo);
	    }
	    
	    
	    
	    
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


		public Page<UserInfo> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection ,String searchTerm) {
			Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
			
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
			
			
		
			return userInfoRepository.findAll(searchTerm,pageable);
			
		}

		public int updateUser(UUID uuid, UserInfo updatedUser) {
			UserInfo userByName = findByUsername(updatedUser.getUsername()).orElse(null);
			UserInfo userByEmail = findByEmail(updatedUser.getEmail()).orElse(null);
			UserInfo userByPhone = findByPhone(updatedUser.getPhone());
			
			//user details in db
			UserInfo userDb = findByUuid(uuid).get();
			
			
			if (userByName!=null && !updatedUser.getUsername().equals(userDb.getUsername())) {
				
				return 1;//username exists
				
		
			}else if (userByEmail!=null && !updatedUser.getEmail().equals(userDb.getEmail())) {
				return 2;//email exists
			}
			else if (userByPhone!=null && !updatedUser.getPhone().equals(userDb.getPhone())) {
				return 3;//phone number exists
		}
			
			
			   System.err.println("all three checks done");
			   
			   System.out.println(updatedUser.getRole().getUuid());
			   
			 
			   System.out.println(updatedUser.getRole().getRoleName());
			   
			  
				
			   
			 
		        //check if there is a change in role
		        if (updatedUser.getRole().getRoleName().equals(userDb.getRole().getRoleName())){
		            updatedUser.getRole().setUuid(userDb.getRole().getUuid());
		        }
		        else{
		            List<Role> allRoles = roleRepository.findAll();
		            for(Role role : allRoles){
		                if(updatedUser.getRole().getRoleName().equals(role.getRoleName())){
		                    updatedUser.getRole().setUuid(role.getUuid());
		                }
		            }
		        }
		        if(updatedUser.getPassword().isEmpty()){
		            updatedUser.setPassword(userDb.getPassword());
		        }
		        else{
		            updatedUser.setPassword(encoder.encode(updatedUser.getPassword()));
		        }
                
		        updatedUser.setEnabled(userDb.isEnabled());
		        updatedUser.setAddresses(userDb.getAddresses());
		        updateUser(updatedUser);

					
			return 0;
			
		}
		
		public void deleteById(UUID uuid) {
			userInfoRepository.deleteById(uuid);
		}

		

		

		
	    
}
