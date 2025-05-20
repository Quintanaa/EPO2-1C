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

	private long id;

	private String username;

	private String email;

	private String token;

	private Set<RoleDTO> roles;
	
}
