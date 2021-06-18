package org.sefglobal.scholarx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sefglobal.scholarx.controller.admin.ProgramController;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.MentorResponse;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.service.ProgramService;
import org.sefglobal.scholarx.util.ProgramState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProgramController.class, org.sefglobal.scholarx.controller.ProgramController.class})
public class ProgramControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private ProgramService programService;
	private final Long programId = 1L;
	private final Program program
			= new Program("SCHOLARX-2020",
			"SCHOLARX program of 2020",
			"http://scholarx/images/SCHOLARX-2020",
			"http://scholarx/SCHOLARX-2020/home",
			ProgramState.CREATED);

	public static Authentication getOauthAuthentication() {
		Profile profile = new Profile();
		profile.setId(1);
		profile.setFirstName("John");
		profile.setLastName("Doe");
		return new OAuth2AuthenticationToken(profile, null, "linkedin");
	}

	@Test
	@WithMockUser(username = "user", authorities = {"ADMIN"})
	void addProgram_withValidData_thenReturns201() throws Exception {
		mockMvc.perform(post("/api/admin/programs")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(program)))
				.andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"ADMIN"})
	void addProgram_withValidData_thenReturnsValidResponseBody() throws Exception {
		mockMvc.perform(post("/api/admin/programs")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(program)))
				.andReturn();

		ArgumentCaptor<Program> programCaptor = ArgumentCaptor.forClass(Program.class);
		verify(programService, times(1)).addProgram(programCaptor.capture());

		program.setState(null);
		String expectedResponse = objectMapper.writeValueAsString(program);
		String actualResponse = objectMapper.writeValueAsString(programCaptor.getValue());
		assertThat(actualResponse).isEqualTo(expectedResponse);
	}

	@Test
	@WithMockUser(username = "user", authorities = {"ADMIN"})
	void updateState_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(put("/api/admin/programs/{id}/state", programId)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(program.getState())))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"ADMIN"})
	void updateState_withUnavailableData_thenReturn404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(programService)
				.updateState(anyLong());

		mockMvc.perform(put("/api/admin/programs/{id}/state", programId)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(program.getState())))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"ADMIN"})
	void updateProgram_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(put("/api/admin/programs/{id}", programId)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(program)))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"ADMIN"})
	void updateProgram_withUnavailableData_thenReturn404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(programService)
				.updateProgram(anyLong(), any(Program.class));

		mockMvc.perform(put("/api/admin/programs/{id}", programId)
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(program)))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"ADMIN"})
	void deleteProgram_withValidData_thenReturns204() throws Exception {
		mockMvc.perform(delete("/api/admin/programs/{id}", programId))
				.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"ADMIN"})
	void deleteProgram_withUnavailableData_thenReturn404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(programService)
				.deleteProgram(anyLong());

		mockMvc.perform(delete("/api/admin/programs/{id}", programId))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getPrograms_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/programs"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getProgramById_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/programs/{id}", programId))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getProgramById_withUnavailableData_thenReturns404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(programService)
				.getProgramById(anyLong());

		mockMvc.perform(get("/api/programs/{id}", programId))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getAllMentorsByProgramId_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/programs/{id}/mentors", programId))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getAllMentorsByProgramId_withUnavailableData_thenReturns404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(programService)
				.getAllMentorsByProgramId(anyLong(), any());

		mockMvc.perform(get("/api/programs/{id}/mentors", programId))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void applyAsMentor_withValidData_thenReturns201() throws Exception {
		mockMvc.perform(post("/api/programs/{id}/mentor", programId)
				.with(authentication(getOauthAuthentication()))
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(new ArrayList<MentorResponse>())))
				.andExpect(status().isCreated());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getLoggedInMentor_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/programs/{id}/mentor", programId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getLoggedInMentor_withUnavailableData_thenReturns404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(programService)
				.getLoggedInMentor(anyLong(), anyLong());

		mockMvc.perform(get("/api/programs/{id}/mentor", programId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getAppliedMentors_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/programs/{id}/mentee/mentors", programId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getAppliedMentors_withUnavailableData_thenReturns204() throws Exception {
		doThrow(NoContentException.class)
				.when(programService)
				.getAppliedMentorsOfMentee(anyLong(), any(), anyLong());

		mockMvc.perform(get("/api/programs/{id}/mentee/mentors", programId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getSelectedMentor_withValidData_thenReturns200() throws Exception {
		mockMvc.perform(get("/api/programs/{id}/mentee/mentor", programId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getSelectedMentor_withUnavailableData_thenReturns404() throws Exception {
		doThrow(ResourceNotFoundException.class)
				.when(programService)
				.getSelectedMentor(anyLong(), anyLong());

		mockMvc.perform(get("/api/programs/{id}/mentee/mentor", programId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "user", authorities = {"DEFAULT"})
	void getSelectedMentor_withUnavailableData_thenReturns204() throws Exception {
		doThrow(NoContentException.class)
				.when(programService)
				.getSelectedMentor(anyLong(), anyLong());

		mockMvc.perform(get("/api/programs/{id}/mentee/mentor", programId)
				.with(authentication(getOauthAuthentication())))
				.andExpect(status().isNoContent());
	}
}
