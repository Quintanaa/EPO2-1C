package com.example.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.springboot.logins.LoginRequest;
import com.example.springboot.logins.LoginResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pokemon.PokemonDTO;
import pokemon.PokemonResponse;

@Service
public class FlaskApiService {

	@Autowired
	private RestTemplate restTemplate;

	private static final String FLASK_API_URL = "http://127.0.0.1:5000/api/v1/ping";

	private String username2;

	private String email;

	private String token;

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
			LoginResponse user = new LoginResponse();

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
	
	public String readFile(MultipartFile file, Model model) {
	    String url = "http://localhost:5000/api/v1/test/file-read";

	    try {
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

	        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

	        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

	        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

	        model.addAttribute("fileUploadResult", response.getBody());
	    } catch (Exception e) {
	        model.addAttribute("fileUploadResult", "Error al enviar archivo a Flask: " + e.getMessage());
	    }

	    return "blog";
	}
	
	public void errorDB(String type, Model model) {
		String url = "http://localhost:5000/api/v1/test/" + type;
		
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			model.addAttribute("dbErrorResult", response.getBody());
		} catch (HttpClientErrorException |HttpServerErrorException e) {
			model.addAttribute("dbErrorResult", "Error desde Flask: " + e.getResponseBodyAsString());
		} catch (ResourceAccessException e) {
			model.addAttribute("dbErrorResult", "No se pudo conectar con el servidor Flask.");
		} catch (Exception e) {
			model.addAttribute("dbErrorResult", "Error inesperado: " + e.getMessage());
		}
	}
	
	public void getPokemon(String name, String error, Model model) {
	    String url = "http://localhost:5000/api/v1/pokemon/pokemon?name=" + name;

	    if (error != null && !error.isEmpty()) {
	        url += "&error=" + error;
	    }

	    try {
            ResponseEntity<PokemonResponse> response = restTemplate.getForEntity(url, PokemonResponse.class);
            PokemonDTO pokemon = response.getBody().getPokemon(); // Accede al Pokémon

	        if (pokemon != null) {
	            model.addAttribute("pokemon", pokemon);
	        } else {
	            model.addAttribute("pokemonError", "No se encontraron datos del Pokémon.");
	        }

	    } catch (HttpClientErrorException | HttpServerErrorException e) {
	        model.addAttribute("pokemonError", "Error de la API: " + e.getResponseBodyAsString());
	    } catch (ResourceAccessException e) {
	        model.addAttribute("pokemonError", "No se pudo conectar con la API Flask.");
	    } catch (Exception e) {
	        model.addAttribute("pokemonError", "Error inesperado: " + e.getMessage());
	    }
	}


}
