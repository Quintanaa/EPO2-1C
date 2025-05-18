package com.example.springboot.dto;

import com.example.springboot.model.Role;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//DTO de los datos sin contraseña
public class LoginResponse {
	@Column
	private String username;
	
	@Column
	private String email;
	
	@Column
	private String token;

	@Column
	private Set<Role> roles;
	
}
