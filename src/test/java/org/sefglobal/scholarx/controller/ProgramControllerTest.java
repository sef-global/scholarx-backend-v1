package org.sefglobal.scholarx.controller;

import org.junit.jupiter.api.Test;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProgramController.class)
public class ProgramControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProgramService programService;
    private final Long programId = 1L;

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
}
