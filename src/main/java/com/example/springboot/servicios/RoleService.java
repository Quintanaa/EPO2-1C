package com.example.springboot.servicios;


import com.example.springboot.Repositorios.RoleRepository;
import com.example.springboot.Repositorios.UsuarioRepository;
import com.example.springboot.model.Role;
import com.example.springboot.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> roleList() {
        return roleRepository.findAll();
    }


}
