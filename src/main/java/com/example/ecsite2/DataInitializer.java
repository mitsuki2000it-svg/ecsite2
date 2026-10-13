package com.example.ecsite2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class DataInitializer implements CommandLineRunner {

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${ADMIN_PASSWORD}")
	private String adminPassword;

	public DataInitializer(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {

		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {

		AppUser admin = appUserRepository.findByUsername("admin").orElse(new AppUser());

		admin.setUsername("admin");

		admin.setPassword(passwordEncoder.encode(adminPassword));

		admin.setRole("ADMIN");

		appUserRepository.save(admin);
	}

}
