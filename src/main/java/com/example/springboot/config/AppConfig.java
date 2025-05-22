package com.example.springboot.config;

import com.example.springboot.model.Role;
import com.example.springboot.model.Usuario;
import com.example.springboot.servicios.RoleService;
import com.example.springboot.servicios.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}

    @Bean
    public CommandLineRunner iniciar(RoleService roleService, UsuarioService usuarioService, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            // Crear roles
            String[] roles = {"ADMIN", "MODERATOR", "USER"};
            for (String nombre : roles) {
                if (roleService.findByNombre(nombre) == null) {
                    Role role = new Role();
                    role.setRole(nombre);
                    roleService.save(role);
                }
            }

            // Usuarios con nombre y contraseña simple (encriptada)
            String[][] usuarios = {
                    {"rubi", "rubi@gmail.com", "rubi", "ADMIN"},
                    {"igna", "igna@gmail.com", "igna", "MODERATOR"},
                    {"oska", "oska@gmail.com", "oska", "USER"},
                    {"inma", "inma@gmail.com", "inma", "USER"}
            };

            for (String[] datos : usuarios) {
                String username = datos[0];
                String email = datos[1];
                String rawPassword = datos[2];
                String rolNombre = datos[3];

                if (usuarioService.getRepo().findByUsername(username) == null) {
                    Usuario user = new Usuario();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(passwordEncoder.encode(rawPassword));

                    Set<Role> rolesAsignados = new HashSet<>();
                    Role rol = roleService.findByNombre(rolNombre);
                    if (rol != null) rolesAsignados.add(rol);

                    user.setRoles(rolesAsignados);
                    usuarioService.saveusr(user);
                    System.out.printf("Usuario %s creado con rol %s%n", username, rolNombre);
                }
            }
        };
    }
}
