package com.example.springboot.controller;

import com.example.springboot.FlaskApiService;
import com.example.springboot.dto.*;
import com.example.springboot.model.Categoria;
import com.example.springboot.model.Producto;
import com.example.springboot.model.Role;
import com.example.springboot.model.Usuario;
import com.example.springboot.psswd.ValidarFormatoPassword;
import com.example.springboot.securityservice.IUsuarioServicio;
import com.example.springboot.servicios.CategoriaService;
import com.example.springboot.servicios.ProductoService;
import com.example.springboot.servicios.RoleService;
import com.example.springboot.servicios.UsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
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

    final ProductoService productoService;

    final CategoriaService categoriaService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public HelloController(IUsuarioServicio userService, UsuarioService usuarioService,
                           RoleService roleService, ProductoService productoService,
                           CategoriaService categoriaService) {
        this.userService = userService;
        this.usuarioService = usuarioService;
        this.roleService = roleService;
        this.productoService = productoService;
        this.categoriaService = categoriaService;
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
            if (selectedRoles != null && selectedRoles.isEmpty()) {
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

    @Secured("ADMIN")
    @GetMapping("/usuarios")
    public String listaUsuarios(ModelMap interfaz) {
        interfaz.addAttribute("usuarios", usuarioService.listUsrDTO());
        return "usuarios";
    }

    @GetMapping("/errorUsers")
    public String accesoDenegado(Model model) {
        model.addAttribute("mensaje", "No tienes permiso para acceder a esta página.");
        return "errorUsers"; // Nombre de la plantilla
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
        return "editarUsers";
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
        return "eliminarUsers";
    }

    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, ModelMap interfaz) {
        usuarioService.getRepo().deleteById(id);
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios/passwd/{id}")
    public String mostrarPasswd(@PathVariable Long id,ModelMap interfaz) {
        interfaz.addAttribute("id", id);
        return "passwd";
    }

    @PostMapping("/usuarios/passwd{id}")
    public String cambiarPasswd(@PathVariable Long id,@RequestParam String viejaPasswd,
                                @RequestParam String nuevaPasswd, @RequestParam String confirmPassword,
                                ModelMap interfaz) {

        Usuario usuario = usuarioService.getRepo().findById(id).orElse(null);

        if (usuario == null) {
            interfaz.addAttribute("error", "Usuario no encontrado.");
            return "cambiarPasswd";
        }

        if(!passwordEncoder.matches(viejaPasswd, usuario.getPassword())) {
            interfaz.addAttribute("error", "La contraseña actual no es correcta.");
            interfaz.addAttribute("id", id);
            return "cambiarPasswd";
        }

        if(!nuevaPasswd.equals(confirmPassword)) {
            interfaz.addAttribute("error", "La contraseñas no coinciden.");
            interfaz.addAttribute("id", id);
            return "cambiarPasswd";
        }

        if(!ValidarFormatoPassword.validarFormato(nuevaPasswd)) {
            interfaz.addAttribute("error", "La contraseña no es válida.");
            interfaz.addAttribute("id", id);
            return "cambiarPasswd";
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPasswd));
        usuarioService.getRepo().save(usuario);

        interfaz.addAttribute("mensaje", "Contraseña actualizada correctamente.");
        return "redirect:/usuarios";
    }

    //Parte de productos
    @GetMapping("/productos")
    public String listarProductos(ModelMap interfaz) {
        interfaz.addAttribute("productos", productoService.listProductoDTO());
        return "productos";
    }

    @GetMapping("/productos/nuevo")
    public String formularioProducto(ModelMap interfaz) {
        interfaz.addAttribute("producto", new Producto());
        interfaz.addAttribute("categorias", categoriaService.categoriaList());
        return "nuevoProducto";
    }

    @PostMapping("/productos/nuevo")
    public String guardarProducto(@ModelAttribute("producto") Producto product, ModelMap interfaz,
                                  @RequestParam(required = false) List<Long> selectedCategorias) {
        if (selectedCategorias != null && selectedCategorias.isEmpty()) {
            Set<Categoria> categorias = new HashSet<>(categoriaService.getCategoriasByIds(selectedCategorias));
            product.setCategorias(categorias);
        }
        productoService.saveProd(product);
        return "redirect:/productos";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProducto(@PathVariable Long id, ModelMap interfaz) {
        Producto producto = productoService.getRepo().findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/productos";
        }

        ProductoDTO productoDTO = new ProductoDTO();
        new ModelMapper().map(producto, productoDTO);

        interfaz.addAttribute("producto", productoDTO);
        interfaz.addAttribute("categorias", categoriaService.categoriaList());
        interfaz.addAttribute("productoCategorias", producto.getCategorias());
        return "editarProducto";
    }

    @PostMapping("/productos/editar/{id}")
    public String guardarProducto(@PathVariable Long id,
                                  @ModelAttribute(name = "producto") ProductoDTO productoDTO,
                                  @RequestParam("selectedCategorias") List<Long> categoriasIds) {
        Producto producto = productoService.getRepo().findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/productos";
        }

        producto.setNombre(productoDTO.getNombre());
        producto.setCantidad(productoDTO.getCantidad());
        producto.setPrecio(productoDTO.getPrecio());

        Set<Categoria> categorias = new HashSet<>(categoriaService.getCategoriasByIds(categoriasIds));
        producto.setCategorias(categorias);

        productoService.getRepo().save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String confirmarEliminarProducto(@PathVariable Long id, ModelMap interfaz) {
        Producto producto = productoService.getRepo().findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/productos";
        }

        ProductoDTO productoDTO = new ProductoDTO();
        new ModelMapper().map(producto, productoDTO);
        interfaz.addAttribute("producto", productoDTO);
        return "eliminarProducto";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, ModelMap interfaz) {
        productoService.getRepo().deleteById(id);
        return "redirect:/productos";
    }

    //Categoría
    @GetMapping("/categorias")
    public String listarCategorias(ModelMap interfaz) {
        interfaz.addAttribute("categorias", categoriaService.listCategoriaDTO());
        return "categorias";
    }

    @GetMapping("/categorias/nuevo")
    public String formularioCategoria(ModelMap interfaz) {
        interfaz.addAttribute("categoria", new Categoria());
        return "nuevaCategoria";
    }

    @PostMapping("/categorias/nuevo")
    public String guardarCategorias(@ModelAttribute("categoria") Categoria categoria, ModelMap interfaz) {
        categoriaService.save(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/categorias/editar/{id}")
    public String editarCategoria(@PathVariable Long id, ModelMap interfaz) {
        Categoria categoria = categoriaService.getRepo().findById(id).orElse(null);
        if (categoria == null) {
            return "redirect:/categorias";
        }

        CategoriaDTO categoriaDTO = new CategoriaDTO();
        new ModelMapper().map(categoria, categoriaDTO);

        interfaz.addAttribute("categoria", categoriaDTO);
        return "editarCategoria";
    }

    @PostMapping("/categorias/editar/{id}")
    public String guardarProducto(@PathVariable Long id,
                                  @ModelAttribute(name = "categoria") CategoriaDTO categoriaDTO) {
        Categoria categoria = categoriaService.getRepo().findById(id).orElse(null);
        if (categoria == null) {
            return "redirect:/categorias";
        }

        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());

        categoriaService.getRepo().save(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/categorias/eliminar/{id}")
    public String confirmarEliminarCategoria(@PathVariable Long id, ModelMap interfaz) {
        Categoria categoria = categoriaService.getRepo().findById(id).orElse(null);
        if (categoria == null) {
            return "redirect:/categorias";
        }

        CategoriaDTO categoriaDTO = new CategoriaDTO();
        new ModelMapper().map(categoria, categoriaDTO);
        interfaz.addAttribute("categoria", categoriaDTO);
        return "eliminarCategoria";
    }

    @PostMapping("/categorias/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id, ModelMap interfaz) {
        categoriaService.getRepo().deleteById(id);
        return "redirect:/categorias";
    }
}
