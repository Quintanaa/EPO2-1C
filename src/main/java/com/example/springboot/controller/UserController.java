package com.example.springboot.controller;

import com.example.springboot.dto.LoginRequest;
import com.example.springboot.dto.LoginResponsePasswd;
import com.example.springboot.model.Usuario;
import com.example.springboot.psswd.ValidarFormatoPassword;
import com.example.springboot.securityservice.IUsuarioServicio;
import com.example.springboot.servicios.RoleService;
import com.example.springboot.servicios.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

public class UserController {

    final IUsuarioServicio userService;

    final UsuarioService usuarioService;

    final RoleService roleService;

    private BCryptPasswordEncoder passwordEncoder;

    public UserController(IUsuarioServicio userService, UsuarioService usuarioService, RoleService roleService) {
        this.userService = userService;
        this.usuarioService = usuarioService;
        this.roleService = roleService;
    }

    @GetMapping("/usuarios")
    public String listaUsuarios(ModelMap interfaz) {
        interfaz.addAttribute("lista", usuarioService.listUsrDTO());
        return "usuarios/lista";
    }

    @GetMapping("/registro")
    public String vistaRegistro(Model interfaz) {
        interfaz.addAttribute("listaRoles", roleService.roleList());
        interfaz.addAttribute("datosUsuario", new LoginResponsePasswd()); // este nombre debe coincidir
        System.out.println("Preparando pantalla registro");
        return "registro";
    }

    @PostMapping("/registro")
    public String guardarRegistro(@ModelAttribute(name = "datosUsuario")LoginResponsePasswd usuarioDTOpasswd) throws Exception {
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
            //El usuario se guarda como no autorizado
            //Guardo la password
            //return "usuarios/detalleusuario";
            return String.format("redirect:/usuarios/%s", usuarioService.saveusr(usuario).getToken());
        } else {
            return "registro";
        }
    }

    @GetMapping("/login")
    public String vistaLogin(ModelMap interfaz) {
        return "usuarios/login";
    }

    @PostMapping("/login")
    public String validarPasswordPst(@ModelAttribute(name = "loginForm")LoginRequest loginRequest){
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
}
