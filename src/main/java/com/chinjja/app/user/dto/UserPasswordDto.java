package com.chinjja.app.user.dto;

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
public class UserPasswordDto {
	@NotNull
	private String oldPassword;
	
	@Size(min = 8, max = 32)
	@NotNull
	private String newPassword;
}
