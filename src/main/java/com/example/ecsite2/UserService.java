package com.example.ecsite2;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {

		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public AppUser register(UserRegisterDto dto) {

		if (appUserRepository.findByUsername(dto.getUsername()).isPresent()) {

			throw new IllegalArgumentException("そのユーザー名は既に使われています");
		}

		AppUser user = new AppUser();

		user.setUsername(dto.getUsername());

		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		user.setRole("USER");

		return appUserRepository.save(user);
	}

}
