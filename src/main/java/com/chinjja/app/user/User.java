package com.chinjja.app.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
public class User {
	@Id
	@GeneratedValue
	@NotNull
	private Long id;
	
	@Email
	@NotNull
	@Column(unique = true)
	private String email;
	
	@NotNull
	@JsonIgnore
	private String password;
	
	private String name;
}
