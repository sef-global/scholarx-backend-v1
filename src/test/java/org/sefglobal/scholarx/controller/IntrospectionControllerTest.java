package org.sefglobal.scholarx.controller;

import org.junit.jupiter.api.Test;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.service.IntrospectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IntrospectionController.class)
public class IntrospectionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IntrospectionService introspectionService;
    private final Long programId = 1L;
    private final Cookie profileIdCookie = new Cookie("profileId", "1");

    @Test
    void getLoggedInUser_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/me")
                .cookie(profileIdCookie))
                .andExpect(status().isOk());
    }

    @Test
    void getLoggedInMentor_withUnavailableData_thenReturns404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(introspectionService)
                .getLoggedInUser(anyLong());

        mockMvc.perform(get("/me")
                .cookie(profileIdCookie))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLoggedInMentor_withUnsuitableData_thenReturns401() throws Exception {
        doThrow(UnauthorizedException.class)
                .when(introspectionService)
                .getLoggedInUser(anyLong());

        mockMvc.perform(get("/me")
                .cookie(profileIdCookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMenteeingPrograms_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/me/programs/mentee")
                .cookie(profileIdCookie))
                .andExpect(status().isOk());
    }

    @Test
    void getMenteeingPrograms_withUnavailableData_thenReturns404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(introspectionService)
                .getMenteeingPrograms(anyLong());

        mockMvc.perform(get("/me/programs/mentee")
                .cookie(profileIdCookie))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMenteeingPrograms_withUnavailableData_thenReturns204() throws Exception {
        doThrow(NoContentException.class)
                .when(introspectionService)
                .getMenteeingPrograms(anyLong());

        mockMvc.perform(get("/me/programs/mentee")
                .cookie(profileIdCookie))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMentoringPrograms_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/me/programs/mentor")
                .cookie(profileIdCookie))
                .andExpect(status().isOk());
    }

    @Test
    void getMentoringPrograms_withUnavailableData_thenReturns404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(introspectionService)
                .getMentoringPrograms(anyLong());

        mockMvc.perform(get("/me/programs/mentor")
                .cookie(profileIdCookie))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMentoringPrograms_withUnavailableData_thenReturns204() throws Exception {
        doThrow(NoContentException.class)
                .when(introspectionService)
                .getMentoringPrograms(anyLong());

        mockMvc.perform(get("/me/programs/mentor")
                .cookie(profileIdCookie))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMentees_withValidData_thenReturns200() throws Exception {
        mockMvc.perform(get("/me/programs/{id}/mentees", programId)
                                .cookie(profileIdCookie))
               .andExpect(status().isOk());
    }

    @Test
    void getMentees_withUnavailableData_thenReturns404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(introspectionService)
                .getMentees(anyLong(), anyLong(), any());

        mockMvc.perform(get("/me/programs/{id}/mentees", programId)
                .cookie(profileIdCookie))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMentees_withUnavailableData_thenReturns204() throws Exception {
        doThrow(NoContentException.class)
                .when(introspectionService)
                .getMentees(anyLong(), anyLong(), any());

        mockMvc.perform(get("/me/programs/{id}/mentees", programId)
                                .cookie(profileIdCookie))
               .andExpect(status().isNoContent());
    }
}
