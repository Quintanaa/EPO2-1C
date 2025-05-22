package com.example.springboot.controller;

import com.example.springboot.dto.LoginRequest;
import com.example.springboot.dto.LoginResponse;
import com.example.springboot.dto.LoginResponsePasswd;
import com.example.springboot.model.Role;
import com.example.springboot.model.Usuario;
import com.example.springboot.psswd.ValidarFormatoPassword;
import com.example.springboot.securityservice.IUsuarioServicio;
import com.example.springboot.servicios.RoleService;
import com.example.springboot.servicios.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {

    final IUsuarioServicio userService;

    final UsuarioService usuarioService;

    final RoleService roleService;

    private BCryptPasswordEncoder passwordEncoder;

    public UserController(IUsuarioServicio userService, UsuarioService usuarioService, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.usuarioService = usuarioService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Secured("ADMIN")
    @GetMapping("/usuarios")
    public String listaUsuarios(ModelMap interfaz) {
        interfaz.addAttribute("usuarios", usuarioService.listUsrDTO());
        return "usuarios/usuarios";
    }

    @GetMapping("/errorUsers")
    public String accesoDenegado(Model model) {
        model.addAttribute("mensaje", "No tienes permiso para acceder a esta página.");
        return "usuarios/errorUsers"; // Nombre de la plantilla
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id, ModelMap interfaz) {
        Usuario usuario = usuarioService.getRepo().findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/usuarios";
        }

        LoginResponse loginResponse = new LoginResponse();
        new ModelMapper().map(usuario, loginResponse);

        Set<Role> roles = usuario.getRoles();

        interfaz.addAttribute("loginResponse", loginResponse);
        interfaz.addAttribute("listaRole", roleService.roleList());
        interfaz.addAttribute("usuarioRoles", usuario.getRoles());
        return "usuarios/editarUsers";
    }

    @PostMapping("/usuarios/editar/{id}")
    public String guardarUsuario(@PathVariable Long id,
                                 @ModelAttribute(name = "loginForm") LoginResponse loginResponse,
                                 @RequestParam("rolesSeleccionados") List<Long> roleIds) {
        Usuario usuario = usuarioService.getRepo().findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/usuarios";
        }

        usuario.setUsername(loginResponse.getUsername());
        usuario.setEmail(loginResponse.getEmail());

        //Debemos gestionar la contraseña a parte

        Set<Role> roles = new HashSet<>(roleService.getRolesByIds(roleIds));
        usuario.setRoles(roles);


        usuarioService.getRepo().save(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String confirmarEliminar(@PathVariable Long id, ModelMap interfaz) {
        Usuario usuario = usuarioService.getRepo().findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/usuarios";
        }

        LoginResponse loginResponse = new LoginResponse();
        new ModelMapper().map(usuario, loginResponse);
        interfaz.addAttribute("loginResponse", loginResponse);
        return "usuarios/eliminarUsers";
    }

    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, ModelMap interfaz) {
        usuarioService.getRepo().deleteById(id);
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/passwd/{id}")
    public String mostrarPasswd(@PathVariable Long id,ModelMap interfaz) {
        interfaz.addAttribute("id", id);
        return "password/passwd";
    }

    @PostMapping("/usuarios/passwd{id}")
    public String cambiarPasswd(@PathVariable Long id,@RequestParam String viejaPasswd,
                                @RequestParam String nuevaPasswd, @RequestParam String confirmPassword,
                                ModelMap interfaz) {

        Usuario usuario = usuarioService.getRepo().findById(id).orElse(null);

        if (usuario == null) {
            interfaz.addAttribute("error", "Usuario no encontrado.");
            return "password/passwd";
        }

        if(!passwordEncoder.matches(viejaPasswd, usuario.getPassword())) {
            interfaz.addAttribute("error", "La contraseña actual no es correcta.");
            interfaz.addAttribute("id", id);
            return "password/passwd";
        }

        if(!nuevaPasswd.equals(confirmPassword)) {
            interfaz.addAttribute("error", "La contraseñas no coinciden.");
            interfaz.addAttribute("id", id);
            return "password/passwd";
        }

        if(!ValidarFormatoPassword.validarFormato(nuevaPasswd)) {
            interfaz.addAttribute("error", "La contraseña no es válida.");
            interfaz.addAttribute("id", id);
            return "password/passwd";
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPasswd));
        usuarioService.getRepo().save(usuario);

        interfaz.addAttribute("mensaje", "Contraseña actualizada correctamente.");
        return "redirect:/usuarios";
    }
}
