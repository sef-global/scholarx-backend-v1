package org.sefglobal.scholarx.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.repository.ProfileRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class IntrospectionServiceTest {
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private MenteeRepository menteeRepository;
    @Mock
    private MentorRepository mentorRepository;
    @InjectMocks
    private IntrospectionService introspectionService;
    final long profileId = 1L;

    @Test
    void getLoggedInUser_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(profileRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> introspectionService.getLoggedInUser(profileId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Profile with id: 1 doesn't exist.");
    }

    @Test
    void getLoggedInUser_withUnsuitableData_thenThrowResourceNotFound() {
        Throwable thrown = catchThrowable(
                () -> introspectionService.getLoggedInUser(-1));
        assertThat(thrown)
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Error, User hasn't logged in.");
    }

    @Test
    void getMenteeingPrograms_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(false)
                .when(profileRepository)
                .existsById(anyLong());

        Throwable thrown = catchThrowable(
                () -> introspectionService.getMenteeingPrograms(profileId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Profile with id: 1 doesn't exist.");
    }

    @Test
    void getMenteeingPrograms_withUnavailableData_thenThrowNoContent() {
        doReturn(true)
                .when(profileRepository)
                .existsById(anyLong());
        doReturn(new ArrayList<>())
                .when(menteeRepository)
                .findAllByProfileId(anyLong());

        Throwable thrown = catchThrowable(
                () -> introspectionService.getMenteeingPrograms(profileId));
        assertThat(thrown)
                .isInstanceOf(NoContentException.class)
                .hasMessage("Error, User has not enrolled in any program as a mentee.");
    }

    @Test
    void getMentoringPrograms_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(false)
                .when(profileRepository)
                .existsById(anyLong());

        Throwable thrown = catchThrowable(
                () -> introspectionService.getMentoringPrograms(profileId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Profile with id: 1 doesn't exist.");
    }

    @Test
    void getMentoringPrograms_withUnavailableData_thenThrowNoContent() {
        doReturn(true)
                .when(profileRepository)
                .existsById(anyLong());
        doReturn(new ArrayList<Mentor>())
                .when(mentorRepository)
                .findAllByProfileId(anyLong());

        Throwable thrown = catchThrowable(
                () -> introspectionService.getMentoringPrograms(profileId));
        assertThat(thrown)
                .isInstanceOf(NoContentException.class)
                .hasMessage("Error, User has not enrolled in any program as a mentor.");
    }
}
