package com.example.springboot;

import com.example.springboot.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.example.springboot.dto.LoginRequest;
import com.example.springboot.dto.LoginResponse;

import com.example.springboot.pokemon.PokemonDTO;
import com.example.springboot.pokemon.PokemonResponse;
import com.example.springboot.servicios.UsuarioService;

@Service
public class FlaskApiService {

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private UsuarioService usuarioService;

	@Value("${api.base-url}")
	private String flaskBaseUrl;

	public String getDataFromFlaskApi() {
		// Llamamos a la API de Flask
		return restTemplate.getForObject(flaskBaseUrl + "/api/v1/ping", String.class);
	}

	public boolean loginToFlask(String username, String password, Model model) {
		String url = flaskBaseUrl + "/api/v1/auth/login";

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

			//Guardamos el usuario en MySQL
			Usuario usuario = new Usuario();
			usuario.setUsername(user.getUsername());
			usuario.setEmail(user.getEmail());
			usuario.setPassword("password");

			usuarioService.registerUsuario(usuario);

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
		String url = flaskBaseUrl + "/api/v1/file/file-read";

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
		String url = flaskBaseUrl + "/api/v1/db/" + type;

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
		String url = flaskBaseUrl + "/api/v1/pokemon/pokemon?name=" + name;

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
