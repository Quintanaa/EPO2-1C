package com.example.springboot.Repositorios;

import com.example.springboot.model.Role;
import com.example.springboot.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
