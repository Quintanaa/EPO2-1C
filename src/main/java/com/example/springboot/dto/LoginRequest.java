package com.example.springboot.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//Se trata del DTO del Login
public class LoginRequest {

	private String username;
	
	private String password;

}
