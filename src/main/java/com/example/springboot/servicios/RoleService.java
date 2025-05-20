package com.example.springboot.servicios;


import com.example.springboot.Repositorios.RoleRepository;
import com.example.springboot.Repositorios.UsuarioRepository;
import com.example.springboot.model.Role;
import com.example.springboot.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public Set<Role> getRolesByIds(List<Long> ids) {
        return new HashSet<Role>(roleRepository.findAllById(ids));
    }

    public Role findByNombre(String nombre){
        return roleRepository.findByRole(nombre);
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

}
