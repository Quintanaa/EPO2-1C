package com.example.springboot.dto;

import com.example.springboot.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//DTO con la contraseña
public class LoginResponsePasswd {

    private long id;

    private String username;

    private String email;

    private String password;

    private Set<RoleDTO> roles;
}
