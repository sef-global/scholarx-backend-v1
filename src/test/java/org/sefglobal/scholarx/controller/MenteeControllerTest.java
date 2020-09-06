package org.sefglobal.scholarx.controller;

import org.junit.jupiter.api.Test;
import org.sefglobal.scholarx.controller.admin.MenteeController;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.service.MenteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MenteeController.class)
public class MenteeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MenteeService menteeService;
    private final Long menteeId = 1L;

    @Test
    void deleteMentee_withValidData_thenReturns204() throws Exception {
        mockMvc.perform(delete("/admin/mentees/{id}", menteeId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMentee_withUnavailableData_thenReturns404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(menteeService)
                .deleteMentee(anyLong());

        mockMvc.perform(delete("/admin/mentees/{id}", menteeId))
                .andExpect(status().isNotFound());
    }
}
