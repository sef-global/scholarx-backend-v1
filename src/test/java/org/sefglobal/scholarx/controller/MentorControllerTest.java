package org.sefglobal.scholarx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sefglobal.scholarx.controller.admin.MentorController;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.service.MentorService;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {MentorController.class, org.sefglobal.scholarx.controller.MentorController.class})
public class MentorControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private MentorService mentorService;
	private final Long mentorId = 1L;
	private final Mentee mentee =
			new Mentee("http://scholarx/SCHOLARX-2020/submission");
	private final Profile profile = new Profile();

	public static Authentication getOauthAuthentication() {
		Profile profile = new Profile();
		profile.setId(1);
		profile.setFirstName("John");
		profile.setLastName("Doe");
		return new OAuth2AuthenticationToken(profile, null, "linkedin");
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMentors_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/mentors"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMentorById_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/mentors/{id}", mentorId))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMentorById_withUnavailableData_thenReturns404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(mentorService)
				.getMentorById(anyLong());

		mockMvc.perform(get("/api/mentors/{id}", mentorId))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"ADMIN"})
	void updateState_withValidData_thenReturns200() throws Exception {
		Map<String, EnrolmentState> payload = new HashMap<>();
		payload.put("state", EnrolmentState.APPROVED);
		mockMvc.perform(put("/api/admin/mentors/{id}/state", mentorId)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"ADMIN"})
	void updateState_withUnavailableData_thenReturn404() throws Exception {
		Map<String, EnrolmentState> payload = new HashMap<>();
		payload.put("state", EnrolmentState.APPROVED);
		doThrow(ResourceNotFoundException.class)
				.when(mentorService)
				.updateState(anyLong(), any(EnrolmentState.class));

		mockMvc.perform(put("/api/admin/mentors/{id}/state", mentorId)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void applyAsMentee_withValidData_thenReturns201() throws Exception {
		mockMvc.perform(post("/api/mentors/{id}/mentee", mentorId)
				.with(authentication(getOauthAuthentication()))
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(mentee)))
				.andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void applyAsMentee_withValidData_thenReturnsValidResponseBody() throws Exception {
		mockMvc.perform(post("/api/mentors/{id}/mentee", mentorId)
				.with(authentication(getOauthAuthentication()))
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(mentee)))
				.andReturn();

		ArgumentCaptor<Mentee> menteeArgumentCaptor = ArgumentCaptor.forClass(Mentee.class);
		verify(mentorService, times(1)).applyAsMentee(anyLong(), anyLong(), menteeArgumentCaptor.capture());

		mentee.setState(null);
		String expectedResponse = objectMapper.writeValueAsString(mentee);
		String actualResponse = objectMapper.writeValueAsString(menteeArgumentCaptor.getValue());
		assertThat(actualResponse).isEqualTo(expectedResponse);
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void applyAsMentee_withUnavailableData_thenReturn404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(mentorService)
				.applyAsMentee(anyLong(), anyLong(), any(Mentee.class));

		mockMvc.perform(post("/api/mentors/{id}/mentee", mentorId)
				.with(authentication(getOauthAuthentication()))
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(mentee)))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void applyAsMentee_withUnavailableData_thenReturn400() throws Exception {
		doThrow(BadRequestException.class)
				.when(mentorService)
				.applyAsMentee(anyLong(), anyLong(), any(Mentee.class));

		mockMvc.perform(post("/api/mentors/{id}/mentee", mentorId)
				.with(authentication(getOauthAuthentication()))
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(mentee)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMenteesOfMentor_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/mentors/{mentorId}/mentees", mentorId))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMenteesOfMentor_withUnavailableData_thenReturns404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(mentorService)
				.getAllMenteesOfMentor(anyLong(), any());

		mockMvc.perform(get("/api/mentors/{mentorId}/mentees", mentorId))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMenteesOfMentor_withUnsuitableData_thenReturns400() throws Exception {
		doThrow(BadRequestException.class)
				.when(mentorService)
				.getAllMenteesOfMentor(anyLong(), any());

		mockMvc.perform(get("/api/mentors/{mentorId}/mentees", mentorId))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void updateMenteeData_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(put("/api/mentors/{mentorId}/mentee", mentorId)
				.with(authentication(getOauthAuthentication()))
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(mentee)))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void updateMenteeData_withUnavailableData_thenReturn404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(mentorService)
				.updateMenteeData(anyLong(), anyLong(), any(Mentee.class));

		mockMvc.perform(put("/api/mentors/{mentorId}/mentee", mentorId)
				.with(authentication(getOauthAuthentication()))
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(mentee)))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void updateMenteeData_withUnsuitableData_thenReturn400() throws Exception {
		doThrow(BadRequestException.class)
				.when(mentorService)
				.updateMenteeData(anyLong(), anyLong(), any(Mentee.class));

		mockMvc.perform(put("/api/mentors/{mentorId}/mentee", mentorId)
				.with(authentication(getOauthAuthentication()))
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(mentee)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getLoggedInMentee_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/mentors/{mentorId}/mentee", mentorId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getLoggedInMentee_withUnavailableData_thenReturns204() throws Exception {

		doThrow(NoContentException.class)
				.when(mentorService)
				.getLoggedInMentee(anyLong(), anyLong());

		mockMvc.perform(get("/api/mentors/{mentorId}/mentee", mentorId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNoContent());

	}
}
