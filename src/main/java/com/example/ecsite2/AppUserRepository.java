package com.example.ecsite2;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
	
	Optional<AppUser> findByUsername(String username);

}
