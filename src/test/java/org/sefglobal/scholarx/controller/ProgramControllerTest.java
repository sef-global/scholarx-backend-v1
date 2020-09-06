package org.sefglobal.scholarx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sefglobal.scholarx.controller.admin.ProgramController;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.service.ProgramService;
import org.sefglobal.scholarx.util.ProgramState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProgramController.class)
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
                .updateState(anyLong(), any(ProgramState.class));

        mockMvc.perform(put("/admin/programs/{id}/state", programId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(program.getState())))
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
    void getAllMentorsByProgramId_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/admin/programs/{id}/mentors", programId))
                .andExpect(status().isOk());
    }

    @Test
    void getAllMentorsByProgramId_withUnavailableData_thenReturns404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(programService)
                .getAllMentorsByProgramId(anyLong());

        mockMvc.perform(get("/admin/programs/{id}/mentors", programId))
               .andExpect(status().isNotFound());
    }
}
