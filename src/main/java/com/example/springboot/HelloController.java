package com.example.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.springboot.servicios.UsuarioService;

@Controller
public class HelloController {

    @Autowired
    private FlaskApiService flaskApiService;

    @Autowired
    private UsuarioService usuarioService;

    //Páginas generales
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
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
        return "blog";
    }

    //Flask-Login
    @GetMapping("/flask-login")
    public String showFlaskLoginForm() {
        return "flask-login";  // Nombre del archivo .html que contiene el formulario
    }

    @PostMapping("/flask-login")
    public String doFlaskLogin(@RequestParam String username, @RequestParam String password, Model model) {
        Boolean res = flaskApiService.loginToFlask(username, password, model);

        if (res) {
            return "redirect:/blog";
        } else {
            return "flask-login";
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
    public String getPokemon(@RequestParam String name,
                             @RequestParam(required = false) String error,
                             Model model) {
        flaskApiService.getPokemon(name, error, model);
        return "blog";
    }

    @GetMapping("/usuarios")
    public String getUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.getAllUsuarios());
        return "usuarios";
    }

}
