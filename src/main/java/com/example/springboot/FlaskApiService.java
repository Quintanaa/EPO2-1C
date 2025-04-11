package com.example.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.example.springboot.logins.LoginRequest;
import com.example.springboot.logins.LoginResponse;

@Service
public class FlaskApiService {

	@Autowired
	private RestTemplate restTemplate;

	private static final String FLASK_API_URL = "http://127.0.0.1:5000/api/v1/ping";

	public String getDataFromFlaskApi() {
		// Llamamos a la API de Flask
		return restTemplate.getForObject(FLASK_API_URL, String.class);
	}

	public boolean loginToFlask(String username, String password, Model model) {
		String url = "http://127.0.0.1:5000/api/v1/auth/login";

		LoginRequest request = new LoginRequest();
		request.setUsername(username);
		request.setPassword(password);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);
		
		try {
			ResponseEntity<LoginResponse> response = restTemplate.postForEntity(url, entity, LoginResponse.class);
			LoginResponse user = response.getBody();

			model.addAttribute("username", user.getUsername());
			model.addAttribute("email", user.getEmail());
			model.addAttribute("token", user.getToken());
			return true;

		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				// Error 400
				model.addAttribute("error", "Datos inválidos enviados al servidor.");
			} else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				// Error 401
				model.addAttribute("error", "Usuario o contraseña incorrectos.");
			} else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				// Error 404
				model.addAttribute("error", "La ruta del servidor no fue encontrada.");
			} else {
				model.addAttribute("error", "Error desconocido: " + e.getMessage());
			}
		} catch (ResourceAccessException e) {
			//No se pudo conectar con Flask
			model.addAttribute("error", "No se pudo conectar con el servidor Flask.");
		} catch (Exception e) {
			model.addAttribute("error", "Ocurrió un error inesperado.");
		}
		
		return false;
	}

}
