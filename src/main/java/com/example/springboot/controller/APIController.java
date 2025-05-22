package com.example.springboot.controller;

import com.example.springboot.servicios.FlaskApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class APIController {

    @Autowired
    private FlaskApiService flaskApiService;

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
}
