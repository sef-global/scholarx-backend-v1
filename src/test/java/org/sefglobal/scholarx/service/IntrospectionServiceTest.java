package org.sefglobal.scholarx.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.repository.ProfileRepository;
import org.sefglobal.scholarx.util.EnrolmentState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
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
    private final Long programId = 1L;
    private final long profileId = 1L;
    private final long mentorId = 1L;
    private final Mentor mentor = new Mentor();
    private final Mentee mentee =
            new Mentee("http://submission.url/");

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
                () -> introspectionService.getMentoringPrograms(profileId, EnrolmentState.APPROVED));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Profile with id: 1 doesn't exist.");
    }

    @Test
    void getMentoringPrograms_withUnavailableData_thenThrowNoContent() {
        doReturn(true)
                .when(profileRepository)
                .existsById(anyLong());

        Throwable thrown = catchThrowable(
                () -> introspectionService.getMentoringPrograms(profileId, EnrolmentState.APPROVED));
        assertThat(thrown)
                .isInstanceOf(NoContentException.class)
                .hasMessage("Error, User has not enrolled in any program as a mentor.");
    }

    @Test
    void getMentees_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(mentorRepository)
                .findByProfileIdAndProgramId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> introspectionService
                        .getMentees(programId, profileId, Collections.emptyList()));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentor by profile id: 1 and program id: 1 doesn't exist.");
    }

    @Test
    void getMentees_withUnavailableData_thenThrowNoContent() {
        Mentor mentor = new Mentor();
        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findByProfileIdAndProgramId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> introspectionService
                        .getMentees(programId, profileId, Collections.emptyList()));
        assertThat(thrown)
                .isInstanceOf(NoContentException.class)
                .hasMessage("No mentees exist for the required program: 1 for the profile: 1");
    }

    @Test
    void confirmMentor_withValidData_thenReturnUpdatedData()
            throws ResourceNotFoundException, BadRequestException {
        mentee.setState(EnrolmentState.APPROVED);
        Program program = new Program();
        program.setId(programId);
        mentor.setProgram(program);
        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findById(anyLong());
        doReturn(Optional.of(mentee))
                .when(menteeRepository)
                .findByProfileIdAndMentorId(anyLong(), anyLong());

        Mentee savedMentee = introspectionService.confirmMentor(mentorId, profileId);
        assertThat(savedMentee).isNotNull();
        assertThat(savedMentee.getState()).isEqualTo(EnrolmentState.APPROVED);
    }

    @Test
    void confirmMentor_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(mentorRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> introspectionService.confirmMentor(mentorId, profileId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentor by id: 1 doesn't exist.");
    }

    @Test
    void confirmMentor_withUnavailableData_thenThrowBadRequest() {
        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findById(anyLong());
        doReturn(Optional.empty())
                .when(menteeRepository)
                .findByProfileIdAndMentorId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> introspectionService.confirmMentor(mentorId, profileId));
        assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Error, User with id: 1 haven't applied for mentor by id: 1.");
    }

    @Test
    void confirmMentor_withUnsuitableData_thenThrowBadRequest() {
        mentee.setState(EnrolmentState.PENDING);
        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findById(anyLong());
        doReturn(Optional.of(mentee))
                .when(menteeRepository)
                .findByProfileIdAndMentorId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> introspectionService.confirmMentor(mentorId, profileId));
        assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Error, User with id: 1 is not approved by the mentor by id: 1.");
    }
}
