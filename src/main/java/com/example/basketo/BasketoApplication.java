package com.example.basketo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

//import com.example.basketo.user.model.Role;
//import com.example.basketo.user.model.UserInfo;
import com.example.basketo.shopadmin.user.repository.RoleRepository;
import com.example.basketo.shopadmin.user.repository.UserInfoRepository;

@SpringBootApplication
public class BasketoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasketoApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(UserInfoRepository userRepository,RoleRepository roleRepository,PasswordEncoder encoder) {
		
		return args->{
			
			
			//roleRepository.deleteAll();

//			Role role = Role.builder().roleName("ROLE_USER").build();
//			Role role2 = Role.builder().roleName("ROLE_ADMIN").build();
//			Role role3 = Role.builder().roleName("ROLE_VERIFIED_USER").build();
//
//			roleRepository.save(role);
//			roleRepository.save(role2);
//			roleRepository.save(role3);
//
//			Optional<Role> userRole = roleRepository.findRoleByName("ROLE_USER");
//			Role role_user = userRole.get();
//
//			Optional<Role> adminRole = roleRepository.findRoleByName("ROLE_ADMIN");
//			Role role_admin = adminRole.get();
//
//			System.out.println(role_admin.getRoleName());
//
//			UserInfo user = UserInfo.builder()
//			        .username("user")
//			        .firstName("us")
//			        .lastName("er")
//			        .email("user@gmail.com")
//			        .password(encoder.encode("password"))
//			        .phone("8848440159")
//			        .role(role_user)
//			        .enabled(true)
//			        .build();
//
//			UserInfo user2 = UserInfo.builder()
//			        .username("luffy")
//			        .firstName("D")
//			        .lastName("luffy")
//			        .email("luffy@gmail.com")
//			        .password(encoder.encode("password"))
//			        .phone("8848440151")
//			        .role(role_user)
//			        .enabled(true)
//			        .build();
//
//			UserInfo user3 = UserInfo.builder()
//			        .username("zoro")
//			        .firstName("Roronoa")
//			        .lastName("Zoro")
//			        .email("zoro@gmail.com")
//			        .password(encoder.encode("password"))
//			        .phone("8848440152")
//			        .role(role_user)
//			        .build();
//
//			UserInfo user4 = UserInfo.builder()
//			        .username("sanji")
//			        .firstName("Vinsmoke")
//			        .lastName("Sanji")
//			        .email("sanji@gmail.com")
//			        .password(encoder.encode("password"))
//			        .phone("8848440153")
//			        .role(role_user)
//			        .build();
//
//			UserInfo admin = UserInfo.builder()
//			        .username("admin1")
//			        .firstName("ahamed")
//			        .lastName("musthaqeem")
//			        .email("musthaqeem@gmail.com")
//			        .password(encoder.encode("password123"))
//			        .phone("8848440178")
//			        .role(role_admin)
//			        .enabled(true)
//			        .build();
//
//			userRepository.save(user);
//			userRepository.save(user2);
//			userRepository.save(user3);
//			userRepository.save(user4);
//			userRepository.save(admin);

	
		
		};
	}
}
