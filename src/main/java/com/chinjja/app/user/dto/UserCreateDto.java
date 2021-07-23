package com.chinjja.app.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.With;

@Value
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Builder
@With
public class UserCreateDto {
	@Email
	@NotNull
	private String email;
	
	@Size(min = 8, max = 32)
	@NotNull
	private String password;
	
	@NotBlank
	private String name;
}
