package com.chinjja.app.security;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.chinjja.app.user.UserRepository;
import com.chinjja.app.user.UserService;
import com.chinjja.app.user.dto.UserCreateDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepository;
	private final UserService userService;

	@PostConstruct
	private void init() {
		if(userRepository.count() == 0L) {
			userService.create(UserCreateDto.builder()
					.email("root@user.com")
					.password("12345678")
					.name("root")
					.build());
		}
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByEmail(username).map(x -> 
			new User(x.getEmail(), x.getPassword(), new ArrayList<>())
		).orElseThrow(() -> new UsernameNotFoundException(username));
	}
}
