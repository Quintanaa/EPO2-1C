package com.example.springboot.servicios;


import com.example.springboot.Repositorios.UsuarioRepository;
import com.example.springboot.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registerUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> loginUsuario(String usuario, String password) {
        return usuarioRepository.findByUsername(usuario).filter(u -> u.getPassword().equals(password));
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }
}
