package com.example.springboot.controller;

import com.example.springboot.dto.*;
import com.example.springboot.model.Role;
import com.example.springboot.model.Usuario;
import com.example.springboot.psswd.ValidarFormatoPassword;
import com.example.springboot.securityservice.IUsuarioServicio;
import com.example.springboot.servicios.RoleService;
import com.example.springboot.servicios.UsuarioService;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class MainController {


    final IUsuarioServicio userService;

    final UsuarioService usuarioService;

    final RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public MainController(IUsuarioServicio userService, UsuarioService usuarioService,
                          RoleService roleService) {
        this.userService = userService;
        this.usuarioService = usuarioService;
        this.roleService = roleService;
    }

    @PostConstruct
    public void addRoles(){
        crearRol("ADMIN");
        crearRol("USER");
        crearRol("MODERATOR");
    }

    private void crearRol(String rol){
        Role role = new Role();
        role.setRole(rol);
        roleService.save(role);
    }

    @PostConstruct
    public void addFirstUser(){
        Usuario user = new Usuario();
        user.setId(37);
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setEmail("email@email.com");

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByNombre("ADMIN"));
        roles.add(roleService.findByNombre("USER"));
        roles.add(roleService.findByNombre("MODERATOR"));
        user.setRoles(roles);

        usuarioService.saveusr(user);
        System.out.println("Usuario creado con todos los roles");
    }

    //Páginas generales
    @GetMapping("/")
    public String index() {
        return "blog";
    }

    @GetMapping("/login")
    public String vistaLogin(ModelMap interfaz) {
        return "usuarios/login";
    }

    @PostMapping("/login")
    public String validarPasswordPst(@ModelAttribute(name = "loginForm") LoginRequest loginRequest){
        String usr = loginRequest.getUsername();
        System.out.println("user: " +usr);
        String password = loginRequest.getPassword();
        System.out.println("password: " +password);
        //¿Es correcta la password?
        if (usuarioService.getRepo().repValidarPassword(usr, passwordEncoder.encode(password)) > 0){
            return "redirect:/blog";
        } else {
            return "usuarios/login";
        }
    }

    @GetMapping("/registro")
    public String vistaRegistro(Model interfaz) {
        interfaz.addAttribute("listaRoles", roleService.roleList());
        interfaz.addAttribute("datosUsuario", new LoginResponsePasswd());
        return "usuarios/registro";
    }

    @PostMapping("/registro")
    public String guardarRegistro(@ModelAttribute(name = "datosUsuario") LoginResponsePasswd usuarioDTOpasswd,
                                  @RequestParam(required = false) List<Long> selectedRoles) throws Exception {
        //Comprobamos el patrón
        System.out.println("Guardando usuario antes: ");
        System.out.println("usuario: " + usuarioDTOpasswd.getUsername() + " password: " + usuarioDTOpasswd.getPassword());
        if (ValidarFormatoPassword.validarFormato(usuarioDTOpasswd.getPassword())) {
            //Convertimos el dto a un elemento del model
            ModelMapper modelMapper = new ModelMapper();
            Usuario usuario = new Usuario();
            modelMapper.map(usuarioDTOpasswd, usuario);

            System.out.println("Buscando usuario");
            System.out.println("Usuario: " + usuarioDTOpasswd.getUsername() + " password: " + usuarioDTOpasswd.getPassword());
            //Codifico la contraseña
            String encodedPassword = userService.getEncodedPassword(usuario);
            usuario.setPassword(encodedPassword);
            //Roles
            if (selectedRoles != null && !selectedRoles.isEmpty()) {
                Set<Role> roles = new HashSet<>(roleService.getRolesByIds(selectedRoles));
                usuario.setRoles(roles);
            } else {
                Set<Role> roles = new HashSet<>();
                roles.add(roleService.findByNombre("USER"));
                usuario.setRoles(roles);
            }
            usuarioService.saveusr(usuario);
            return String.format("redirect:/login", usuarioService.saveusr(usuario).getToken());
        } else {
            return "usuarios/registro";
        }
    }

    //Blog
    @GetMapping("/blog")
    public String showBlog(Model model) {
        model.addAttribute("userList", usuarioService.listUsrDTO());
        return "blog";
    }

}
