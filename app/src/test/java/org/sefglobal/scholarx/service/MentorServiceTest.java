package org.sefglobal.scholarx.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.repository.ProfileRepository;
import org.sefglobal.scholarx.util.EnrolmentState;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class MentorServiceTest {
    @Mock
    private MentorRepository mentorRepository;
    @Mock
    private MenteeRepository menteeRepository;
    @Mock
    private ProfileRepository profileRepository;
    @InjectMocks
    private MentorService mentorService;
    private final Long mentorId = 1L;
    private final Long programId = 1L;
    private final Long profileId = 1L;
    private final Mentor mentor = new Mentor();
    private final Profile profile = new Profile();
    private final Mentee mentee =
            new Mentee("http://submission.url/");

    @Test
    void getMentorById_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(mentorRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> mentorService.getMentorById(mentorId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentor by id: 1 doesn't exist.");
    }

    @Test
    void updateState_withValidData_thenReturnUpdatedData()
            throws ResourceNotFoundException {
        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findById(anyLong());
        doReturn(mentor)
                .when(mentorRepository)
                .save(any(Mentor.class));

        Mentor savedMentor = mentorService.updateState(mentorId, EnrolmentState.APPROVED);
        assertThat(savedMentor).isNotNull();
    }

    @Test
    void applyAsMentee_withValidData_thenReturnCreatedData()
            throws ResourceNotFoundException, BadRequestException {
        final Mentor mentor = new Mentor();
        mentor.setState(EnrolmentState.APPROVED);

        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findById(anyLong());
        doReturn(Optional.of(profile))
                .when(profileRepository)
                .findById(anyLong());
        doReturn(mentee)
                .when(menteeRepository)
                .save(any(Mentee.class));

        Mentee savedMentee = mentorService.applyAsMentee(programId, profileId, mentee);
        assertThat(savedMentee).isNotNull();
    }

    @Test
    void applyAsMentee_withUnavailableMentor_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(mentorRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> mentorService.applyAsMentee(programId, profileId, mentee));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Unable to apply as a mentee. " +
                            "Mentor with id: 1 doesn't exist.");
    }

    @Test
    void applyAsMentee_withUnsuitableData_thenThrowBadRequest() {
        final Mentor mentor = new Mentor();
        mentor.setState(EnrolmentState.PENDING);

        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> mentorService.applyAsMentee(programId, profileId, mentee));
        assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Error, Unable to apply as a mentee. " +
                            "Mentor with id: 1 is not in the applicable state.");
    }

    @Test
    void applyAsMentee_withUnavailableProfile_thenThrowResourceNotFound() {
        final Mentor mentor = new Mentor();
        mentor.setState(EnrolmentState.APPROVED);

        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findById(anyLong());
        doReturn(Optional.empty())
                .when(profileRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> mentorService.applyAsMentee(programId, profileId, mentee));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Unable to apply as a mentee. " +
                            "Profile with id: 1 doesn't exist.");
    }

    @Test
    void getAllMenteesOfMentor_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(mentorRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> mentorService.getAllMenteesOfMentor(mentorId, Optional.empty()));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentor by id: 1 doesn't exist.");
    }

    @Test
    void getAllMenteesOfMentor_withUnsuitableData_thenThrowBadRequest() {
        mentor.setState(EnrolmentState.PENDING);
        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> mentorService.getAllMenteesOfMentor(mentorId, Optional.empty()));
        assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Error, Mentor by id: 1 is not an approved mentor.");
    }

    @Test
    void updateMenteeData_withValidData_thenReturnUpdatedData()
            throws ResourceNotFoundException, BadRequestException {
        mentee.setState(EnrolmentState.PENDING);
        mentee.setMentor(mentor);
        doReturn(Optional.of(mentee))
                .when(menteeRepository)
                .findByProfileIdAndMentorId(anyLong(), anyLong());
        doReturn(mentee)
                .when(menteeRepository)
                .save(any(Mentee.class));

        Mentee savedMentee = mentorService.updateMenteeData(profileId, mentorId, mentee);
        assertThat(savedMentee).isNotNull();
    }

    @Test
    void updateMenteeData_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(menteeRepository)
                .findByProfileIdAndMentorId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> mentorService.updateMenteeData(profileId, mentorId, mentee));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentee by profile id: 1 and mentor id: 1 cannot be updated. " +
                            "Mentee doesn't exist.");
    }

    @Test
    void updateMenteeData_withUnsuitableData_thenThrowBadRequest() {
        mentee.setState(EnrolmentState.APPROVED);
        doReturn(Optional.of(mentee))
                .when(menteeRepository)
                .findByProfileIdAndMentorId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> mentorService.updateMenteeData(profileId, mentorId, mentee));
        assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Error, Application cannot be updated. " +
                            "Mentee is not in a valid state.");
    }

    @Test
    void getLoggedInMentee_withUnavailableData_thenThrowNoContent() {
        doReturn(Optional.empty())
                .when(menteeRepository)
                .findByProfileIdAndMentorId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> mentorService.getLoggedInMentee(mentorId, profileId));
        assertThat(thrown)
                .isInstanceOf(NoContentException.class)
                .hasMessage("Error, User by profile id: 1 " +
                            "hasn't applied for mentor with id: 1.");
    }
}
