package com.chinjja.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.chinjja.app.user.User;
import com.chinjja.app.user.dto.UserCreateDto;
import com.chinjja.app.user.dto.UserEmailDto;
import com.chinjja.app.user.dto.UserPasswordDto;
import com.chinjja.app.user.dto.UserUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.val;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@WithMockUser("root@user.com")
@Transactional
public class UserMvcTests {
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	MockMvc mvc;
	
	@Test
	void whenGetAllUser_when() throws Exception {
		mvc.perform(
					get("/api/users")
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isMethodNotAllowed());
	}
	
	@Test
	void one() throws Exception {
		val resByEmail = mvc.perform(
				get("/api/users/search/by-email")
					.param("email", "root@user.com")
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse();
		
		val userByEmail = objectMapper.readValue(resByEmail.getContentAsByteArray(), User.class);
		assertThat(userByEmail.getEmail()).isEqualTo("root@user.com");
		
		val resById = mvc.perform(
				get("/api/users/{id}", userByEmail.getId())
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse();
		
		val userById = objectMapper.readValue(resById.getContentAsByteArray(), User.class);
		assertThat(userById).isEqualTo(userByEmail);
	}
	
	@Test
	void forbidden_by_id() throws Exception {
		mvc.perform(
				get("/api/users/{id}", 9999)
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}
	
	@Test
	void forbidden_by_email() throws Exception {
		mvc.perform(
				get("/api/users/search/by-email")
					.param("email", "root1@user.com")
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}
	
	@Test
	void change_email() throws Exception {
		val dto = UserEmailDto.builder()
				.email("admin@user.com")
				.build();
		val content = objectMapper.writeValueAsBytes(dto);
		mvc.perform(
				put("/api/users/{id}/email", 1)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
				.andExpect(status().isNoContent());
		
		mvc.perform(
				get("/api/users/search/by-email")
				.param("email", "admin@user.com")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	void change_email_bad() throws Exception {
		val dto = UserEmailDto.builder()
				.email("adminuser.com")
				.build();
		val content = objectMapper.writeValueAsBytes(dto);
		mvc.perform(
				put("/api/users/{id}/email", 1)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
				.andExpect(status().isBadRequest());
		
		val res = mvc.perform(
				get("/api/users/search/by-email")
				.param("email", "root@user.com")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse();
		
		val user = objectMapper.readValue(res.getContentAsByteArray(), User.class);
		assertThat(user.getEmail()).isEqualTo("root@user.com");
	}
	
	@Test
	void change_password() throws Exception {
		val dto = UserPasswordDto.builder()
				.oldPassword("12345678")
				.newPassword("87654321")
				.build();
		val content = objectMapper.writeValueAsBytes(dto);
		mvc.perform(
				put("/api/users/{id}/password", 1)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
				.andExpect(status().isNoContent());
	}
	
	@Test
	void change_password_bad() throws Exception {
		val dto = UserPasswordDto.builder()
				.oldPassword("1234567")
				.newPassword("87654321")
				.build();
		val content = objectMapper.writeValueAsBytes(dto);
		mvc.perform(
				put("/api/users/{id}/password", 1)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@WithAnonymousUser
	void create() throws Exception {
		val dto = UserCreateDto.builder()
				.email("user@user.com")
				.password("12345678")
				.name("user")
				.build();
		val content = objectMapper.writeValueAsBytes(dto);
		val res = mvc.perform(
					post("/api/users")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse();
		
		val user = objectMapper.readValue(res.getContentAsByteArray(), User.class);
		assertThat(user.getEmail()).isEqualTo("user@user.com");
	}
	
	@Test
	@WithAnonymousUser
	void duplicate() throws Exception {
		val dto = UserCreateDto.builder()
				.email("root@user.com")
				.password("12345678")
				.name("root")
				.build();
		val content = objectMapper.writeValueAsBytes(dto);
		mvc.perform(
					post("/api/users")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isConflict());
	}
	
	@Test
	void update() throws Exception {
		val res = mvc.perform(
				get("/api/users/search/by-email")
					.param("email", "root@user.com")
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse();
		val user = objectMapper.readValue(res.getContentAsByteArray(), User.class);
		
		val dto = UserUpdateDto.builder()
				.name("user")
				.build();
		val content = objectMapper.writeValueAsBytes(dto);
		mvc.perform(
					patch("/api/users/{id}", user.getId())
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isNoContent());
		
		val res2 = mvc.perform(get("/api/users/{id}", user.getId()))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse();
		val user2 = objectMapper.readValue(res2.getContentAsByteArray(), User.class);
		assertThat(user2.getName()).isEqualTo("user");
	}
	
	@Test
	void update_not_found() throws Exception {
		val dto = UserUpdateDto.builder()
				.name("user")
				.build();
		val content = objectMapper.writeValueAsBytes(dto);
		mvc.perform(
					patch("/api/users/{id}", 9999)
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isNotFound());
	}
	
	@Test
	@WithAnonymousUser
	void unauthorized_get() throws Exception {
		mvc.perform(get("/api/users/1"))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithAnonymousUser
	void unauthorized_patch() throws Exception {
		val dto = UserUpdateDto.builder()
				.name("user")
				.build();
		val content = objectMapper.writeValueAsBytes(dto);
		mvc.perform(patch("/api/users/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithAnonymousUser
	void unauthorized_delete() throws Exception {
		mvc.perform(delete("/api/users/1"))
				.andExpect(status().isUnauthorized());
	}
}
