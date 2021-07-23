package com.chinjja.app.web;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.chinjja.app.user.User;
import com.chinjja.app.user.UserService;
import com.chinjja.app.user.dto.UserCreateDto;
import com.chinjja.app.user.dto.UserEmailDto;
import com.chinjja.app.user.dto.UserPasswordDto;
import com.chinjja.app.user.dto.UserUpdateDto;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;
	
	@GetMapping("/{id}")
	public User one(
			@PathVariable Long id) {
		return userService.byId(id);
	}
	
	@GetMapping("/search/by-email{email}")
	public User search(
			@RequestParam String email) {
		val user = userService.byEmail(email);
		if(user == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return user;
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public User create(
			@RequestBody @Valid UserCreateDto dto) {
		return userService.create(dto);
	}
	
	@PatchMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void patch(
			@PathVariable("id") User user,
			@RequestBody @Valid UserUpdateDto dto) {
		userService.update(user, dto);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@PathVariable("id") User user) {
		userService.delete(user);
	}
	
	@PutMapping("/{id}/email")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void email(
			@PathVariable("id") User user,
			@RequestBody @Valid UserEmailDto dto) {
		userService.updateEmail(user, dto);
	}
	
	@PutMapping("/{id}/password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void password(
			@PathVariable("id") User user,
			@RequestBody @Valid UserPasswordDto dto) {
		userService.updatePassword(user, dto);
	}
	
	@ExceptionHandler(MissingPathVariableException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void massingPathVariable(MissingPathVariableException e) {
		
	}
}
