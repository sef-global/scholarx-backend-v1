package org.sefglobal.scholarx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.sefglobal.scholarx.controller.admin.MentorController;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.service.MentorService;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MentorController.class)
public class MentorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MentorService mentorService;
    private final Long mentorId = 1L;

    @Test
    void updateState_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(put("/admin/mentors/{id}/state", mentorId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(EnrolmentState.APPROVED)))
                .andExpect(status().isOk());
    }

    @Test
    void updateState_withUnavailableData_thenReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(mentorService)
                .updateState(anyLong(), any(EnrolmentState.class));

        mockMvc.perform(put("/admin/mentors/{id}/state", mentorId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(EnrolmentState.APPROVED)))
                .andExpect(status().isNotFound());
    }
}
