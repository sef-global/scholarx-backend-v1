package org.sefglobal.scholarx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sefglobal.scholarx.controller.admin.MentorController;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.service.MentorService;
import org.sefglobal.scholarx.util.EnrolmentState;
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
    final Cookie profileIdCookie = new Cookie("profileId", "1");

    @Test
    void getMentors_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/mentors"))
                .andExpect(status().isOk());
    }

    @Test
    void getMentorById_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/mentors/{id}", mentorId))
                .andExpect(status().isOk());
    }

    @Test
    void getMentorById_withUnavailableData_thenReturns404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(mentorService)
                .getMentorById(anyLong());

        mockMvc.perform(get("/mentors/{id}", mentorId))
                .andExpect(status().isNotFound());
    }

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
                .cookie(profileIdCookie)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentee)))
                .andExpect(status().isCreated());
    }

    @Test
    void applyAsMentee_withValidData_thenReturnsValidResponseBody() throws Exception {
        mockMvc.perform(post("/mentors/{id}/mentee", mentorId)
                .cookie(profileIdCookie)
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
                .cookie(profileIdCookie)
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
                .cookie(profileIdCookie)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentee)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMenteesOfMentor_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/mentors/{mentorId}/mentees", mentorId))
                .andExpect(status().isOk());
    }

    @Test
    void getMenteesOfMentor_withUnavailableData_thenReturns404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(mentorService)
                .getAllMenteesOfMentor(anyLong(), any());

        mockMvc.perform(get("/mentors/{mentorId}/mentees", mentorId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMenteesOfMentor_withUnsuitableData_thenReturns400() throws Exception {
        doThrow(BadRequestException.class)
                .when(mentorService)
                .getAllMenteesOfMentor(anyLong(), any());

        mockMvc.perform(get("/mentors/{mentorId}/mentees", mentorId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMenteeData_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(put("/mentors/{mentorId}/mentee", mentorId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentee)))
                .andExpect(status().isOk());
    }

    @Test
    void updateMenteeData_withUnavailableData_thenReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(mentorService)
                .updateMenteeData(anyLong(), anyLong(), any(Mentee.class));

        mockMvc.perform(put("/mentors/{mentorId}/mentee", mentorId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentee)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMenteeData_withUnsuitableData_thenReturn400() throws Exception {
        doThrow(BadRequestException.class)
                .when(mentorService)
                .updateMenteeData(anyLong(), anyLong(), any(Mentee.class));

        mockMvc.perform(put("/mentors/{mentorId}/mentee", mentorId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mentee)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLoggedInMentee_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/mentors/{mentorId}/mentee", mentorId))
                .andExpect(status().isOk());
    }

    @Test
    void getLoggedInMentee_withUnavailableData_thenReturns204() throws Exception {
        doThrow(NoContentException.class)
                .when(mentorService)
                .getLoggedInMentee(anyLong(), anyLong());

        mockMvc.perform(get("/mentors/{mentorId}/mentee", mentorId))
                .andExpect(status().isNoContent());
    }
}
