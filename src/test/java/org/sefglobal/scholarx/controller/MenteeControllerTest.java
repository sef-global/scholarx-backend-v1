package org.sefglobal.scholarx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.service.MenteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {MenteeController.class, org.sefglobal.scholarx.controller.admin.MenteeController.class})
public class MenteeControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private MenteeService menteeService;
	private final Long menteeId = 1L;

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
	@WithMockUser(username = "admin", authorities = {"ADMIN"})
	void approveOrRejectMentee_withValidData_thenReturns200() throws Exception {
		Map<String, Boolean> payload = new HashMap<>();
		payload.put("isApproved", true);
		mockMvc.perform(put("/api/mentees/{menteeId}/state", menteeId)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"ADMIN"})
	void approveOrRejectMentee_withUnavailableData_thenReturn404() throws Exception {
		Map<String, Boolean> payload = new HashMap<>();
		payload.put("isApproved", true);
		doThrow(ResourceNotFoundException.class)
				.when(menteeService)
				.approveOrRejectMentee(anyLong(), anyBoolean());

		mockMvc.perform(put("/api/mentees/{menteeId}/state", menteeId)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "admin", authorities = {"ADMIN"})
	void approveOrRejectMentee_withUnsuitableData_thenReturn400() throws Exception {
		Map<String, Boolean> payload = new HashMap<>();
		payload.put("isApproved", true);
		doThrow(BadRequestException.class)
				.when(menteeService)
				.approveOrRejectMentee(anyLong(), anyBoolean());

		mockMvc.perform(put("/api/mentees/{menteeId}/state", menteeId)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(payload)))
				.andExpect(status().isBadRequest());
	}
}
