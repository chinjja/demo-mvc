package com.chinjja.app.user.impl;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.chinjja.app.user.User;
import com.chinjja.app.user.UserRepository;
import com.chinjja.app.user.UserService;
import com.chinjja.app.user.dto.UserCreateDto;
import com.chinjja.app.user.dto.UserEmailDto;
import com.chinjja.app.user.dto.UserPasswordDto;
import com.chinjja.app.user.dto.UserUpdateDto;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper mapper = new ModelMapper() {{
		getConfiguration().setSkipNullEnabled(true);
	}};

	@Override
	public void init() {
		if(userRepository.count() == 0L) {
			create(UserCreateDto.builder()
					.email("root@user.com")
					.password("12345678")
					.name("root")
					.build());
		}
	}
	
	@Override
	public User create(@Valid UserCreateDto dto) {
		if(byEmail(dto.getEmail()) != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
		val user = new User();
		mapper.map(dto, user);
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		return userRepository.save(user);
	}
	
	@Override
	public User update(@Valid User user, @Valid UserUpdateDto dto) {
		mapper.map(dto, user);
		return userRepository.save(user);
	}
	
	@Override
	public User updatePassword(@Valid User user, @Valid UserPasswordDto dto) {
		if(!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		return userRepository.save(user);
	}
	
	@Override
	public User updateEmail(@Valid User user, @Valid UserEmailDto dto) {
		user.setEmail(dto.getEmail());
		val authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return userRepository.save(user);
	}
	
	@Override
	public void delete(@Valid User user) {
		userRepository.delete(user);
	}
	
	@Override
	public User byId(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public User byEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}
}
