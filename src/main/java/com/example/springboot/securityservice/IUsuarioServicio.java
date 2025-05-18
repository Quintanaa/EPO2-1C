package com.example.springboot.securityservice;

import com.example.springboot.model.Usuario;

public interface IUsuarioServicio {
    public String getEncodedPassword(Usuario usuario);
}
