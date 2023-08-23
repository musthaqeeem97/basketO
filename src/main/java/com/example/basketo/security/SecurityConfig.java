package com.example.basketo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;





@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	UserDetailsService jpaUserDetailsService() {
		
		return new JPAUserDetailsService();
	}
	

	@Autowired
	AuthenticationSuccessHandler successHandler;
	

	
	
	
	@Bean
	public SecurityFilterChain  securityFilterChain(HttpSecurity http) throws Exception {
		
		
		http.csrf().disable()
        .authorizeHttpRequests((authorize)->authorize.antMatchers("/signup","/login","/verifyOtp","/signup/verifyOtp","/test/**","/css/**").permitAll()
        		.antMatchers("/login/otpVerification").hasAuthority("ROLE_USER")
        		.antMatchers("/adminpanel/**").hasAuthority("ROLE_ADMIN")
        		.anyRequest().hasAnyAuthority("ROLE_VERIFIED_USER","ROLE_ADMIN"))
        
        .formLogin().loginPage("/login").permitAll().successHandler(successHandler)
       
    	;
		
		
		
		return http.build();
		
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	public AuthenticationProvider authenticationProvider(){
		//giving info about who is the user details service and password encoder
	        //these info can be used to generate user details object and set it to authentication object.
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(jpaUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
	   
		return daoAuthenticationProvider;
	}
}
