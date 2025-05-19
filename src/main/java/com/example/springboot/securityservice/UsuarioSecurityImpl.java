package com.example.springboot.securityservice;

import com.example.springboot.Repositorios.UsuarioRepository;
import com.example.springboot.model.Role;
import com.example.springboot.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UsuarioSecurityImpl implements IUsuarioServicio, UserDetailsService {

    final UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UsuarioSecurityImpl(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String getEncodedPassword(Usuario usuario) {
        String password = usuario.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        return encodedPassword;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername usuario: " + username);
        Usuario usuario = usuarioRepository.findByUsername(username);
        System.out.println("loadUserByUsername usuario: " + usuario.getUsername());
        System.out.println(usuario.getPassword());

        User springUser = null;

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : usuario.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
        }
        springUser = new org.springframework.security.core.userdetails.User(username, usuario.getPassword(), grantedAuthorities);
        return springUser;
    }
}
