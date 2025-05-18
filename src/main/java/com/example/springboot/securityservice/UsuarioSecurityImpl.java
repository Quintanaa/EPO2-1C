package com.example.springboot.securityservice;

import com.example.springboot.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UsuarioSecurityImpl implements IUsuarioServicio{

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public String getEncodedPassword(Usuario usuario) {
        String password = usuario.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        return encodedPassword;
    }
}
