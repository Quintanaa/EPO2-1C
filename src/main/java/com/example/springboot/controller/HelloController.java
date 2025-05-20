package com.example.springboot.controller;

import com.example.springboot.FlaskApiService;
import com.example.springboot.dto.LoginRequest;
import com.example.springboot.dto.LoginResponse;
import com.example.springboot.dto.LoginResponsePasswd;
import com.example.springboot.dto.RoleDTO;
import com.example.springboot.model.Role;
import com.example.springboot.model.Usuario;
import com.example.springboot.psswd.ValidarFormatoPassword;
import com.example.springboot.securityservice.IUsuarioServicio;
import com.example.springboot.servicios.RoleService;
import com.example.springboot.servicios.UsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class HelloController {

    @Autowired
    private FlaskApiService flaskApiService;

    final IUsuarioServicio userService;

    final UsuarioService usuarioService;

    final RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public HelloController(IUsuarioServicio userService, UsuarioService usuarioService, RoleService roleService) {
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
        usuarioService.saveusr(user);
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByNombre("ADMIN"));
        roles.add(roleService.findByNombre("USER"));
        roles.add(roleService.findByNombre("MODERATOR"));
        user.setRoles(roles);

    }

    //Páginas generales
    @GetMapping("/")
    public String index() {
        return "blog";
    }

    @GetMapping("/login")
    public String vistaLogin(ModelMap interfaz) {
        return "login";
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
            return "login";
        }
    }

    @GetMapping("/registro")
    public String vistaRegistro(Model interfaz) {
        interfaz.addAttribute("listaRoles", roleService.roleList());
        interfaz.addAttribute("datosUsuario", new LoginResponsePasswd());
        return "registro";
    }

    @PostMapping("/registro")
    public String guardarRegistro(@ModelAttribute(name = "datosUsuario") LoginResponsePasswd usuarioDTOpasswd) throws Exception {
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
            //Probar con esto
            usuarioService.saveusr(usuario);
            return String.format("redirect:/login", usuarioService.saveusr(usuario).getToken());
        } else {
            return "registro";
        }
    }

    //Blog
    @GetMapping("/blog")
    public String showBlog(Model model) {
        try {
            String flaskApiData = flaskApiService.getDataFromFlaskApi();
            model.addAttribute("apiData", flaskApiData);
        } catch (Exception e) {
            model.addAttribute("apiData", "Error al conectar con la API Flask: " + e.getMessage());
        }
        model.addAttribute("userList", usuarioService.listUsrDTO());
        return "blog";
    }

    //Flask-Login
    @GetMapping("/flask-login")
    public String showFlaskLoginForm() {
        return "login";  // Nombre del archivo .html que contiene el formulario
    }


    @PostMapping("/flask-login")
    public String doFlaskLogin(@RequestParam String username, @RequestParam String password, Model model,
                               HttpSession session) {
        Boolean res = flaskApiService.loginToFlask(username, password, model);

        if (res) {
            // Guardamos el usuario autenticado en la sesión
            session.setAttribute("loggedInUser", username);
            return "redirect:/blog";
        } else {
            return "login";
        }
    }


    //Subir archivos
    @PostMapping("/file-upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        return flaskApiService.readFile(file, model);
    }

    //Errores de la base de datos
    @GetMapping("/db-error")
    public String testDB(@RequestParam String type, Model model) {
        flaskApiService.errorDB(type, model);
        return "blog";
    }

    //Consulta de pokemons
    @GetMapping("/pokemon")
    public String getPokemon(@RequestParam String name, @RequestParam(required = false) String error, Model model) {
        flaskApiService.getPokemon(name, error, model);
        return "blog";
    }

    @GetMapping("/usuarios")
    public String listaUsuarios(ModelMap interfaz) {
        interfaz.addAttribute("usuarios", usuarioService.listUsrDTO());
        return "usuarios";
    }

    @GetMapping("usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id, ModelMap interfaz) {
        Usuario usuario = usuarioService.getRepo().findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/usuarios";
        }

        LoginResponse loginResponse = new LoginResponse();
        new ModelMapper().map(usuario, loginResponse);
        interfaz.addAttribute("loginResponse", loginResponse);
        return "editarUsers";
    }

    @PostMapping("usuarios/editar/{id}")
    public String guardarUsuario(@PathVariable Long id,
                                 @ModelAttribute(name = "loginForm") LoginResponse loginResponse,
                                 @RequestParam("roles") List<Long> roleIds) {
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

    @GetMapping("usuarios/eliminar/{id}")
    public String confirmarEliminar(@PathVariable Long id, ModelMap interfaz) {
        Usuario usuario = usuarioService.getRepo().findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/usuarios";
        }

        LoginResponse loginResponse = new LoginResponse();
        new ModelMapper().map(usuario, loginResponse);
        interfaz.addAttribute("loginResponse", loginResponse);
        return "eliminarUsers";
    }

    @PostMapping("usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, ModelMap interfaz) {
        usuarioService.getRepo().deleteById(id);
        return "redirect:/usuarios";
    }
}
