package org.sefglobal.scholarx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.model.Comment;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.service.MenteeService;
import org.sefglobal.scholarx.service.CommentService;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {MenteeController.class, org.sefglobal.scholarx.controller.admin.MenteeController.class})
public class MenteeControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private MenteeService menteeService;
	@MockBean
	private CommentService commentService;
	private final Long menteeId = 1L;
	private final Long mentorId = 1L;
	private final Long commentId = 1L;

	private final Comment comment = new Comment();


	public static Authentication getOauthAuthentication() {
		Profile profile = new Profile();
		profile.setId(1);
		profile.setFirstName("John");
		profile.setLastName("Doe");
		return new OAuth2AuthenticationToken(profile, null, "linkedin");
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"ADMIN"})
	void deleteMentee_withValidData_thenReturns204() throws Exception {
		mockMvc.perform(delete("/api/admin/mentees/{id}", menteeId))
				.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"ADMIN"})
	void deleteMentee_withUnavailableData_thenReturns404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(menteeService)
				.deleteMentee(anyLong());

		mockMvc.perform(delete("/api/admin/mentees/{id}", menteeId))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void approveOrRejectMentee_withValidData_thenReturns200() throws Exception {
		Map<String, Boolean> payload = new HashMap<>();
		payload.put("isApproved", true);
		mockMvc.perform(put("/api/mentees/{menteeId}/state", menteeId)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(payload))
						.with(authentication(getOauthAuthentication())))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void approveOrRejectMentee_withUnavailableData_thenReturn404() throws Exception {
		Map<String, Boolean> payload = new HashMap<>();
		payload.put("isApproved", true);
		doThrow(ResourceNotFoundException.class)
				.when(menteeService)
				.approveOrRejectMentee(anyLong(), anyLong(), anyBoolean());

		mockMvc.perform(put("/api/mentees/{menteeId}/state", menteeId)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(payload))
						.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void approveOrRejectMentee_withUnsuitableData_thenReturn403() throws Exception {
		Map<String, Boolean> payload = new HashMap<>();
		payload.put("isApproved", true);
		doThrow(UnauthorizedException.class)
				.when(menteeService)
				.approveOrRejectMentee(anyLong(), anyLong(), anyBoolean());

		mockMvc.perform(put("/api/mentees/{menteeId}/state", menteeId)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(payload))
						.with(authentication(getOauthAuthentication())))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void approveOrRejectMentee_withUnsuitableData_thenReturn400() throws Exception {
		Map<String, Boolean> payload = new HashMap<>();
		payload.put("isApproved", true);
		doThrow(BadRequestException.class)
				.when(menteeService)
				.approveOrRejectMentee(anyLong(), anyLong(), anyBoolean());

		mockMvc.perform(put("/api/mentees/{menteeId}/state", menteeId)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(payload))
						.with(authentication(getOauthAuthentication())))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"ADMIN"})
	void updateAssignedMentor_withValidData_thenReturns200() throws Exception {
		Map<String, Long> payload = new HashMap<>();
		payload.put("mentorId", mentorId);
		mockMvc.perform(put("/api/admin/mentees/{menteeId}/assign", menteeId)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"ADMIN"})
	void updateAssignedMentor_withUnavailableData_thenReturns404() throws Exception {
		Map<String, Long> payload = new HashMap<>();
		payload.put("mentorId", mentorId);
		doThrow(ResourceNotFoundException.class)
				.when(menteeService)
				.updateAssignedMentor(anyLong(), anyLong());

		mockMvc.perform(put("/api/admin/mentees/{menteeId}/assign", menteeId)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"ADMIN"})
	void updateAssignedMentor_withUnsuitableData_thenReturns400() throws Exception {
		Map<String, Long> payload = new HashMap<>();
		payload.put("mentorId", mentorId);
		doThrow(BadRequestException.class)
				.when(menteeService)
				.updateAssignedMentor(anyLong(), anyLong());

		mockMvc.perform(put("/api/admin/mentees/{menteeId}/assign", menteeId)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void addComment_withValidData_thenReturn201() throws Exception {
		mockMvc.perform(post("/api/mentees/{id}/comments",menteeId)
						.with(authentication(getOauthAuthentication()))
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(comment)))
				.andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void addComment_withValidData_thenReturnsValidResponseBody() throws Exception {
		mockMvc.perform(post("/api/mentees/{id}/comments",menteeId)
						.with(authentication(getOauthAuthentication()))
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(comment)))
				.andReturn();

		ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
		verify(commentService, times(1)).addMenteeComment(anyLong(),anyLong(),commentCaptor.capture());

		String expectedResponse = objectMapper.writeValueAsString(comment);
		String actualResponse = objectMapper.writeValueAsString(commentCaptor.getValue());
		assertThat(actualResponse).isEqualTo(expectedResponse);
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMenteeComments_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/mentees/{id}/comments",menteeId)
						.with(authentication(getOauthAuthentication())))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getMenteeComments_withUnavailableData_thenReturns404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(commentService)
				.getAllMenteeComments(anyLong(),anyLong());

		mockMvc.perform(get("/api/mentees/{id}/comments", menteeId)
						.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void deleteMenteeComment_withValidData_thenReturns204() throws Exception {
		mockMvc.perform(delete("/api/mentees//comment/{id}", commentId)
						.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void deleteMenteeComment_withUnavailableData_thenReturns404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(commentService)
				.deleteComment(anyLong(),anyLong());

		mockMvc.perform(delete("/api/mentees//comment/{id}", commentId)
						.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void updateComment_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(put("/api/mentees/comment/{id}", commentId)
						.contentType("application/json")
						.with(authentication(getOauthAuthentication()))
						.content(objectMapper.writeValueAsString(comment)))
				.andExpect(status().isOk());
	}
}
