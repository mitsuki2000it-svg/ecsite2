package com.example.ecsite2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(auth -> auth.requestMatchers("/admin.html", "/admin.js", "/sales/**")
				.hasRole("ADMIN")

				.requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")

				.requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")

				.requestMatchers(HttpMethod.PUT, "/orders/**").hasRole("ADMIN")

				.requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")

				.requestMatchers("/register", "/register.html", "/register.js", "/login.js", "/csrf").permitAll()

				.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login.html").loginProcessingUrl("/login")
						.defaultSuccessUrl("/index.html", true).failureUrl("/login.html?error").permitAll())
				.logout(logout -> logout.logoutSuccessUrl("/login.html?logout").permitAll());

		return http.build();
	}

}
