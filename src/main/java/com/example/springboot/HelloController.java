package com.example.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.springboot.logins.LoginResponse;

@Controller
public class HelloController {

	@Autowired
	private FlaskApiService flaskApiService;
	
	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}

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
    
    @PostMapping("/file-upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        return flaskApiService.readFile(file, model);
    }
    
    @GetMapping("/db-error")
    public String testDB(@RequestParam String type, Model model) {
    	flaskApiService.errorDB(type, model);
    	return "blog";
    }

    /*    @GetMapping("/flasklogin")
    public String showFlaskData() {
        return "flasklogin";
    }

    @PostMapping("/flasklogin")
    public String doFlaskLog(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {
        LoginResponse res = flaskApiService.loginToFlask(username, password, model);

        if (res != null) {
            model.addAttribute("username", res.getUsername());
            model.addAttribute("email", res.getEmail());
            model.addAttribute("token", res.getToken());
        } else {
            // Solo mostramos el mensaje de error
            model.addAttribute("error", "Credenciales incorrectas o error al conectar con la API Flask");
        }

        return "blog";
    } */

}
