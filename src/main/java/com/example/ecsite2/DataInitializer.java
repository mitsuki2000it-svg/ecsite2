package com.example.ecsite2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;

	public DataInitializer(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {

		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {

		if (appUserRepository.findByUsername("admin").isEmpty()) {

			AppUser admin = new AppUser();

			admin.setUsername("admin");

			admin.setPassword(passwordEncoder.encode("admin1234"));

			admin.setRole("ADMIN");

			appUserRepository.save(admin);
		}

	}
}
