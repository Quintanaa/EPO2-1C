package com.example.springboot.servicios;


import com.example.springboot.Repositorios.UsuarioRepository;
import com.example.springboot.dto.LoginResponse;
import com.example.springboot.model.Usuario;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Service
public class UsuarioService {

    @Autowired
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario registerUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /*public Optional<Usuario> loginUsuario(String usuario, String password) {
        return usuarioRepository.findByUsername(usuario).filter(u -> u.getPassword().equals(password));
    }*/

    /*public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }*/

    public List<LoginResponse> listUsrDTO(){
        ModelMapper modelMapper = new ModelMapper();
        List<Usuario> usuarios = usuarioRepository.findAll();
        //Recorro la lista
        ListIterator iterator = usuarios.listIterator();
        //Creo la lista de DTOs
        List <LoginResponse> responses = new ArrayList<>();
        while (iterator.hasNext()) {
            Usuario usuario = (Usuario) iterator.next();
            LoginResponse loginResponse = new LoginResponse();
            modelMapper.map(usuario, loginResponse);
            listUsrDTO().add(loginResponse);
        }
        return listUsrDTO();
    }

    public LoginResponse saveusr(Usuario usuario) {
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        ModelMapper modelMapper = new ModelMapper();
        LoginResponse loginResponse = new LoginResponse();
        modelMapper.map(usuarioGuardado, loginResponse);
        return loginResponse;
    }

    public UsuarioRepository getRepo(){
        return usuarioRepository;
    }
}
