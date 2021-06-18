package org.sefglobal.scholarx.controller;

import org.junit.jupiter.api.Test;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.service.IntrospectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthUserController.class)
public class AuthUserControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private IntrospectionService introspectionService;
	private final Long programId = 1L;
	private final Long mentorId = 1L;

	public static Authentication getOauthAuthentication() {
		Profile profile = new Profile();
		profile.setId(1);
		profile.setFirstName("John");
		profile.setLastName("Doe");
		return new OAuth2AuthenticationToken(profile, null, "linkedin");
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMenteeingPrograms_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/me/programs/mentee")
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMenteeingPrograms_withUnavailableData_thenReturns404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(introspectionService)
				.getMenteeingPrograms(anyLong());

		mockMvc.perform(get("/api/me/programs/mentee")
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMenteeingPrograms_withUnavailableData_thenReturns204() throws Exception {
		doThrow(NoContentException.class)
				.when(introspectionService)
				.getMenteeingPrograms(anyLong());

		mockMvc.perform(get("/api/me/programs/mentee")
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMentoringPrograms_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/me/programs/mentor")
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMentoringPrograms_withUnavailableData_thenReturns404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(introspectionService)
				.getMentoringPrograms(anyLong(), any());

		mockMvc.perform(get("/api/me/programs/mentor")
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMentoringPrograms_withUnavailableData_thenReturns204() throws Exception {
		doThrow(NoContentException.class)
				.when(introspectionService)
				.getMentoringPrograms(anyLong(), any());

		mockMvc.perform(get("/api/me/programs/mentor")
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMentees_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/me/programs/{id}/mentees", programId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMentees_withUnavailableData_thenReturns404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(introspectionService)
				.getMentees(anyLong(), anyLong(), any());

		mockMvc.perform(get("/api/me/programs/{id}/mentees", programId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMentees_withUnavailableData_thenReturns204() throws Exception {
		doThrow(NoContentException.class)
				.when(introspectionService)
				.getMentees(anyLong(), anyLong(), any());

		mockMvc.perform(get("/api/me/programs/{id}/mentees", programId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void confirmMentor_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(put("/api/me/mentor/{mentorId}/confirmation", mentorId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void confirmMentor_withUnavailableData_thenReturn404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(introspectionService)
				.confirmMentor(anyLong(), anyLong());

		mockMvc.perform(put("/api/me/mentor/{mentorId}/confirmation", mentorId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void confirmMentor_withUnsuitableData_thenReturn400() throws Exception {
		doThrow(BadRequestException.class)
				.when(introspectionService)
				.confirmMentor(anyLong(), anyLong());

		mockMvc.perform(put("/api/me/mentor/{mentorId}/confirmation", mentorId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isBadRequest());
	}
}
