package com.chinjja.app.user;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.chinjja.app.user.dto.UserCreateDto;
import com.chinjja.app.user.dto.UserEmailDto;
import com.chinjja.app.user.dto.UserPasswordDto;
import com.chinjja.app.user.dto.UserUpdateDto;

@Validated
@Transactional(readOnly = true)
public interface UserService {
	@Transactional
	public User create(@Valid UserCreateDto dto);
	
	@Transactional
	public void init();
	
	@Transactional
	@PreAuthorize("isAuthenticated() and #user.email == principal.username")
	public User update(@Valid User user, @Valid UserUpdateDto dto);
	
	@Transactional
	@PreAuthorize("isFullyAuthenticated() and #user.email == principal.username")
	public User updatePassword(@Valid User user, @Valid UserPasswordDto dto);
	
	@Transactional
	@PreAuthorize("isAuthenticated() and #user.email == principal.username")
	public User updateEmail(@Valid User user, @Valid UserEmailDto dto);
	
	@Transactional
	@PreAuthorize("isAuthenticated() and #user.email == principal.username")
	public void delete(@Valid User user);

	@PostAuthorize("isAuthenticated() and returnObject?.email == principal.username")
	public User byId(Long id);

	@PostAuthorize("isAuthenticated() and returnObject?.email == principal.username")
	public User byEmail(String email);
}
