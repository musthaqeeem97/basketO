package com.example.basketo.shopadmin.user.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {

	private UUID uuid;
	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private String phoneNumber;
	private String password;
	private String newPassword;
    private String newPasswordRe;
	private String confirmPassword;
}
