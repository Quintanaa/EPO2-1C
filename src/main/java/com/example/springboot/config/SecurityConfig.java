package com.example.springboot.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http.formLogin(form -> form
				.loginPage("/login")
						.failureUrl("/login?error=true")
						.defaultSuccessUrl("/blog", true)
						.permitAll()
		);
		http.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/blog")
				//Extras
		);
		http.authorizeHttpRequests()
				.requestMatchers("/js/**", "/css/**", "/assets/**", "/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/**").permitAll()
				.anyRequest().authenticated()

				.and()
				.exceptionHandling()
				.accessDeniedHandler(accessDeniedHandler())

				.and()
				.csrf().disable()
				.cors().disable()
				.authenticationProvider(authenticationProvider());

		return http.build();
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return (request, response, accessDeniedException) -> {
			// Redirige a la ruta que sirve la plantilla de error
			response.sendRedirect("/errorUsers");
		};
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
		return authenticationProvider;
	}

	@Bean
	static GrantedAuthorityDefaults grantedAuthorityDefaults() {return new GrantedAuthorityDefaults("ROLE_");}
}
