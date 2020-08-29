package org.sefglobal.scholarx.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.util.EnrolmentState;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class MentorServiceTest {
    @Mock
    private MentorRepository mentorRepository;
    @InjectMocks
    private MentorService mentorService;
    private final Long mentorId = 1L;
    private final Mentor mentor =
            new Mentor();

    @Test
    void updateState_withValidData_thenReturnUpdatedData() throws ResourceNotFoundException {
        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findById(anyLong());
        doReturn(mentor)
                .when(mentorRepository)
                .save(any(Mentor.class));

        Mentor savedMentor = mentorService.updateState(mentorId, EnrolmentState.APPROVED);
        assertThat(savedMentor).isNotNull();
    }
}
