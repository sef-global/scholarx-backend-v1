package org.sefglobal.scholarx.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.util.EnrolmentState;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class MenteeServiceTest {
    @Mock
    private MenteeRepository menteeRepository;
    @InjectMocks
    private MenteeService menteeService;
    private final Long menteeId = 1L;
    private final Long profileId = 1L;
    private final Mentor mentor = new Mentor();

    @Test
    void deleteMentee_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(menteeRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> menteeService.deleteMentee(menteeId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentee with id: 1 cannot be deleted. " +
                            "Mentee doesn't exist.");
    }

    @Test
    void approveOrRejectMentee_withValidData_thenReturnUpdatedData()
            throws ResourceNotFoundException, BadRequestException, UnauthorizedException {
        final Mentee mentee = new Mentee();
        mentee.setState(EnrolmentState.PENDING);
        Profile profile = new Profile();
        profile.setId(profileId);
        mentor.setProfile(profile);
        mentee.setAssignedMentor(mentor);

        doReturn(Optional.of(mentee))
                .when(menteeRepository)
                .findById(anyLong());
        doReturn(mentee)
                .when(menteeRepository)
                .save(any(Mentee.class));

        Mentee savedMentee = menteeService.approveOrRejectMentee(menteeId, profileId, true);
        assertThat(savedMentee).isNotNull();
        assertThat(savedMentee.getState()).isEqualTo(EnrolmentState.APPROVED);
    }

    @Test
    void approveOrRejectMentee_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(menteeRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> menteeService.approveOrRejectMentee(menteeId, profileId, true));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentee cannot be approved/rejected. " +
                            "Mentee with id: 1 doesn't exist.");
    }

    @Test
    void approveOrRejectMentee_withUnsuitableData_thenThrowUnauthorized() {
        final Mentee mentee = new Mentee();
        mentee.setState(EnrolmentState.PENDING);
        mentor.setProfile(new Profile());
        mentee.setAssignedMentor(mentor);

        doReturn(Optional.of(mentee))
                .when(menteeRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> menteeService.approveOrRejectMentee(menteeId, profileId, true));
        assertThat(thrown)
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Error, Mentee cannot be approved/rejected. " +
                            "Mentee with id: 1 is not a mentee of " +
                            "mentor with profile id: 1.");
    }

    @Test
    void approveOrRejectMentee_withUnsuitableData_thenThrowBadRequest() {
        final Mentee mentee = new Mentee();
        mentee.setState(EnrolmentState.REMOVED);
        Profile profile = new Profile();
        profile.setId(profileId);
        mentor.setProfile(profile);
        mentee.setAssignedMentor(mentor);

        doReturn(Optional.of(mentee))
                .when(menteeRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> menteeService.approveOrRejectMentee(menteeId, profileId, true));
        assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Error, Mentee cannot be approved/rejected. " +
                            "Mentee with id: 1 is removed.");
    }
}
