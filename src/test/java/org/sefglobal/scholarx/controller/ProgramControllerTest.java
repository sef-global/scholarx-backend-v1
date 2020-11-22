package org.sefglobal.scholarx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sefglobal.scholarx.controller.admin.ProgramController;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.service.ProgramService;
import org.sefglobal.scholarx.util.ProgramState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
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
    private final Mentor mentor
            = new Mentor("Sample application",
                         "Sample prerequisites");
    final Cookie profileIdCookie = new Cookie("profileId", "1");

    @Test
    void addProgram_withValidData_thenReturns201() throws Exception {
        mockMvc.perform(post("/admin/programs")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(program)))
               .andExpect(status().isCreated());
    }

    @Test
    void addProgram_withValidData_thenReturnsValidResponseBody() throws Exception {
        mockMvc.perform(post("/admin/programs")
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
    void updateState_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(put("/admin/programs/{id}/state", programId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(program.getState())))
               .andExpect(status().isOk());
    }

    @Test
    void updateState_withUnavailableData_thenReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(programService)
                .updateState(anyLong());

        mockMvc.perform(put("/admin/programs/{id}/state", programId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(program.getState())))
               .andExpect(status().isNotFound());
    }

    @Test
    void updateProgram_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(put("/admin/programs/{id}", programId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(program)))
               .andExpect(status().isOk());
    }

    @Test
    void updateProgram_withUnavailableData_thenReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(programService)
                .updateProgram(anyLong(), any(Program.class));

        mockMvc.perform(put("/admin/programs/{id}", programId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(program)))
               .andExpect(status().isNotFound());
    }

    @Test
    void deleteProgram_withValidData_thenReturns204() throws Exception {
        mockMvc.perform(delete("/admin/programs/{id}", programId))
               .andExpect(status().isNoContent());
    }

    @Test
    void deleteProgram_withUnavailableData_thenReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(programService)
                .deleteProgram(anyLong());

        mockMvc.perform(delete("/admin/programs/{id}", programId))
               .andExpect(status().isNotFound());
    }

    @Test
    void getPrograms_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/programs"))
                .andExpect(status().isOk());
    }

    @Test
    void getProgramById_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/programs/{id}", programId))
                .andExpect(status().isOk());
    }

    @Test
    void getProgramById_withUnavailableData_thenReturns404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(programService)
                .getProgramById(anyLong());

        mockMvc.perform(get("/programs/{id}", programId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllMentorsByProgramId_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/programs/{id}/mentors", programId))
                .andExpect(status().isOk());
    }

    @Test
    void getAllMentorsByProgramId_withUnavailableData_thenReturns404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(programService)
                .getAllMentorsByProgramId(anyLong());

        mockMvc.perform(get("/programs/{id}/mentors", programId))
               .andExpect(status().isNotFound());
    }

    @Test
    void applyAsMentor_withValidData_thenReturns201() throws Exception {
        mockMvc.perform(post("/programs/{id}/mentor", programId)
                .cookie(profileIdCookie)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentor)))
                .andExpect(status().isCreated());
    }

    @Test
    void applyAsMentor_withValidData_thenReturnsValidResponseBody() throws Exception {
        mockMvc.perform(post("/programs/{id}/mentor", programId)
                .cookie(profileIdCookie)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentor)))
                .andReturn();

        ArgumentCaptor<Mentor> mentorArgumentCaptor = ArgumentCaptor.forClass(Mentor.class);
        verify(programService, times(1)).applyAsMentor(anyLong(), anyLong(), mentorArgumentCaptor.capture());

        mentor.setState(null);
        String expectedResponse = objectMapper.writeValueAsString(mentor);
        String actualResponse = objectMapper.writeValueAsString(mentorArgumentCaptor.getValue());
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void applyAsMentor_withUnavailableData_thenReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(programService)
                .applyAsMentor(anyLong(), anyLong(), any(Mentor.class));

        mockMvc.perform(post("/programs/{id}/mentor", programId)
                .cookie(profileIdCookie)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentor)))
                .andExpect(status().isNotFound());
    }

    @Test
    void applyAsMentor_withUnavailableData_thenReturn400() throws Exception {
        doThrow(BadRequestException.class)
                .when(programService)
                .applyAsMentor(anyLong(), anyLong(), any(Mentor.class));

        mockMvc.perform(post("/programs/{id}/mentor", programId)
                .cookie(profileIdCookie)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentor)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLoggedInMentor_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/programs/{id}/mentor", programId)
                .cookie(profileIdCookie))
                .andExpect(status().isOk());
    }

    @Test
    void getLoggedInMentor_withUnavailableData_thenReturns404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(programService)
                .getLoggedInMentor(anyLong(), anyLong());

        mockMvc.perform(get("/programs/{id}/mentor", programId)
                .cookie(profileIdCookie))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMentorData_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(put("/programs/{programId}/application", programId)
                .cookie(profileIdCookie)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentor)))
                .andExpect(status().isOk());
    }

    @Test
    void updateMentorData_withUnavailableData_thenReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(programService)
                .updateMentorData(anyLong(), anyLong(), any(Mentor.class));

        mockMvc.perform(put("/programs/{programId}/application", programId)
                .cookie(profileIdCookie)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentor)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAppliedMentors_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/programs/{id}/mentee/mentors", programId)
                .cookie(profileIdCookie))
                .andExpect(status().isOk());
    }

    @Test
    void getAppliedMentors_withUnavailableData_thenReturns204() throws Exception {
        doThrow(NoContentException.class)
                .when(programService)
                .getAppliedMentorsOfMentee(anyLong(), anyLong());

        mockMvc.perform(get("/programs/{id}/mentee/mentors", programId)
                .cookie(profileIdCookie))
                .andExpect(status().isNoContent());
    }

    @Test
    void getSelectedMentor_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/programs/{id}/mentee/mentor", programId)
                .cookie(profileIdCookie))
                .andExpect(status().isOk());
    }

    @Test
    void getSelectedMentor_withUnavailableData_thenReturns404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(programService)
                .getSelectedMentor(anyLong(), anyLong());

        mockMvc.perform(get("/programs/{id}/mentee/mentor", programId)
                .cookie(profileIdCookie))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSelectedMentor_withUnavailableData_thenReturns204() throws Exception {
        doThrow(NoContentException.class)
                .when(programService)
                .getSelectedMentor(anyLong(), anyLong());

        mockMvc.perform(get("/programs/{id}/mentee/mentor", programId)
                .cookie(profileIdCookie))
                .andExpect(status().isNoContent());
    }
}
