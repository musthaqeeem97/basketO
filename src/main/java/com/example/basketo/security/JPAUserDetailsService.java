package com.example.basketo.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.basketo.shopadmin.user.model.UserInfo;
import com.example.basketo.shopadmin.user.repository.UserInfoRepository;


@Service
public class JPAUserDetailsService implements UserDetailsService{

	@Autowired
	private UserInfoRepository userRepository;
	
	

	@Override
    public UserDetails loadUserByUsername(String usernameOrEmail ) throws UsernameNotFoundException {
		
		System.err.println("in user details service");
		System.err.println( usernameOrEmail + "hi");
		
		
		
		
		if(userRepository.findByEmail(usernameOrEmail.toLowerCase()).isPresent()) {
			
			UserInfo user_input = userRepository.findByEmail(usernameOrEmail.toLowerCase()).get();
			
			if (!user_input.isEnabled()) {
				
	            throw new DisabledException("User account is disabled.");
	      
		}
		
		System.err.println(userRepository.findByEmail(usernameOrEmail).get().getUsername());
		
		
		UserInfo user = userRepository.findByEmail(usernameOrEmail)
        		
        		
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        System.err.println("in user details service 2..........");

        // Create and return the custom User instance
        return new CustomUser(
        		user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                getAuthorities(user.getRole().getRoleName()),
                user.getEmail(),
                user.getOtp()
        );
        
		}
		
			System.err.println(userRepository.findByUsername(usernameOrEmail).get().getUsername());
			
			
	       UserInfo user = userRepository.findByUsername(usernameOrEmail)
	        		
	        		
	                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	       
	       
	       UserInfo user_input = userRepository.findByUsername(usernameOrEmail.toLowerCase()).get();
			
			if (!user_input.isEnabled()) {
				
	            throw new DisabledException("User account is disabled.");
	      
		}
	        
	        System.err.println("in user details service 3..........");

	        // Create and return the custom User instance
	        return new CustomUser(
	        		user.getUsername(),
	                user.getPassword(),
	                true,
	                true,
	                true,
	                true,
	                getAuthorities(user.getRole().getRoleName()),
	                user.getEmail(),
	                user.getOtp()
	        );
			
		
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

}