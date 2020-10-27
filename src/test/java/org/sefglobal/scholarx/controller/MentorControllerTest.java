package org.sefglobal.scholarx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sefglobal.scholarx.controller.admin.MentorController;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.service.MentorService;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    void applyAsMentee_withValidData_thenReturns201() throws Exception {
        mockMvc.perform(post("/mentors/{id}/mentee", mentorId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentee)))
                .andExpect(status().isCreated());
    }

    @Test
    void applyAsMentee_withValidData_thenReturnsValidResponseBody() throws Exception {
        mockMvc.perform(post("/mentors/{id}/mentee", mentorId)
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
    void applyAsMentee_withUnavailableData_thenReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(mentorService)
                .applyAsMentee(anyLong(), anyLong(), any(Mentee.class));

        mockMvc.perform(post("/mentors/{id}/mentee", mentorId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentee)))
                .andExpect(status().isNotFound());
    }

    @Test
    void applyAsMentee_withUnavailableData_thenReturn400() throws Exception {
        doThrow(BadRequestException.class)
                .when(mentorService)
                .applyAsMentee(anyLong(), anyLong(), any(Mentee.class));

        mockMvc.perform(post("/mentors/{id}/mentee", mentorId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentee)))
                .andExpect(status().isBadRequest());
    }
}
