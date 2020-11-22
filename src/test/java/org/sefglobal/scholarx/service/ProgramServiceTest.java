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
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.repository.ProfileRepository;
import org.sefglobal.scholarx.repository.ProgramRepository;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.sefglobal.scholarx.util.ProgramState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ProgramServiceTest {
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private MentorRepository mentorRepository;
    @Mock
    private MenteeRepository menteeRepository;
    @InjectMocks
    private ProgramService programService;
    private final Long programId = 1L;
    private final Long profileId = 1L;
    private final Program program =
            new Program("SCHOLARX-2020", "SCHOLARX program of 2020",
                        "http://scholarx/images/SCHOLARX-2020",
                        "http://scholarx/SCHOLARX-2020/home", ProgramState.CREATED);
    private final Mentor mentor =
            new Mentor("Sample application",
                       "Sample prerequisites");
    private final Profile profile =
            new Profile();

    @Test
    void updateState_withValidData_thenReturnUpdatedData()
            throws ResourceNotFoundException {
        doReturn(Optional.of(program))
                .when(programRepository)
                .findById(anyLong());
        doReturn(program)
                .when(programRepository)
                .save(any(Program.class));

        Program savedProgram = programService.updateState(programId);
        assertThat(savedProgram).isNotNull();
    }

    @Test
    void updateState_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(programRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.updateState(programId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Program with id: 1 cannot be updated. " +
                            "Program doesn't exist.");
    }

    @Test
    void updateProgram_withValidData_thenReturnUpdatedData()
            throws ResourceNotFoundException {
        doReturn(Optional.of(program))
                .when(programRepository)
                .findById(anyLong());
        doReturn(program)
                .when(programRepository)
                .save(any(Program.class));

        Program savedProgram = programService.updateProgram(programId, program);
        assertThat(savedProgram).isNotNull();
    }

    @Test
    void updateProgram_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(programRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.updateProgram(programId,program));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Program with id: 1 cannot be updated. " +
                            "Program doesn't exist.");
    }

    @Test
    void deleteProgram_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(programRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.deleteProgram(programId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Program with id: 1 cannot be deleted. " +
                            "Program doesn't exist.");
    }

    @Test
    void getAllMentorsByProgramId_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(false)
                .when(programRepository)
                .existsById(anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.getAllMentorsByProgramId(programId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Program by id: 1 doesn't exist");
    }

    @Test
    void getProgramById_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(programRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.getProgramById(programId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Program by id: 1 doesn't exist.");
    }

    @Test
    void applyAsMentor_withValidData_thenReturnCreatedData()
            throws ResourceNotFoundException, BadRequestException {
        final Program program =
                new Program("SCHOLARX-2020", "SCHOLARX program of 2020",
                            "http://scholarx/images/SCHOLARX-2020",
                            "http://scholarx/SCHOLARX-2020/home",
                            ProgramState.MENTOR_APPLICATION);

        doReturn(Optional.of(program))
                .when(programRepository)
                .findById(anyLong());
        doReturn(Optional.of(profile))
                .when(profileRepository)
                .findById(anyLong());
        doReturn(mentor)
                .when(mentorRepository)
                .save(any(Mentor.class));

        Mentor savedMentor = programService.applyAsMentor(programId, profileId, mentor);
        assertThat(savedMentor).isNotNull();
    }

    @Test
    void applyAsMentor_withUnavailableProgram_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(programRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.applyAsMentor(programId, profileId, mentor));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Unable to apply as a mentor. " +
                            "Program with id: 1 doesn't exist.");
    }

    @Test
    void applyAsMentor_withUnsuitableData_thenThrowBadRequest() {
        doReturn(Optional.of(program))
                .when(programRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.applyAsMentor(programId, profileId, mentor));
        assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Error, Unable to apply as a mentor. " +
                            "Program with id: 1 is not in the applicable status.");
    }

    @Test
    void applyAsMentor_withUnavailableProfile_thenThrowResourceNotFound() {
        final Program program =
                new Program("SCHOLARX-2020", "SCHOLARX program of 2020",
                        "http://scholarx/images/SCHOLARX-2020",
                        "http://scholarx/SCHOLARX-2020/home",
                        ProgramState.MENTOR_APPLICATION);

        doReturn(Optional.of(program))
                .when(programRepository)
                .findById(anyLong());
        doReturn(Optional.empty())
                .when(profileRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.applyAsMentor(programId, profileId, mentor));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Unable to apply as a mentor. " +
                            "Profile with id: 1 doesn't exist.");
    }

    @Test
    void getLoggedInMentor_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(mentorRepository)
                .findByProfileIdAndProgramId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.getLoggedInMentor(programId, profileId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentor by profile id: 1 " +
                            "and program id: 1 doesn't exist.");
    }

    @Test
    void updateMentorData_withValidData_thenReturnUpdatedData()
            throws ResourceNotFoundException, BadRequestException {
        mentor.setState(EnrolmentState.PENDING);
        program.setState(ProgramState.MENTOR_APPLICATION);
        mentor.setProgram(program);
        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findByProfileIdAndProgramId(anyLong(), anyLong());
        doReturn(mentor)
                .when(mentorRepository)
                .save(any(Mentor.class));

        Mentor savedMentor = programService.updateMentorData(profileId, programId, mentor);
        assertThat(savedMentor).isNotNull();
    }

    @Test
    void updateMentorData_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(mentorRepository)
                .findByProfileIdAndProgramId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.updateMentorData(profileId, programId, mentor));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentor by profile id: 1 and program id: 1 cannot be updated. " +
                            "Mentor doesn't exist.");
    }

    @Test
    void updateMentorData_withUnsuitableData_thenThrowBadRequest() {
        mentor.setState(EnrolmentState.APPROVED);
        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findByProfileIdAndProgramId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.updateMentorData(profileId, programId, mentor));
        assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Error, Application cannot be updated. " +
                            "Mentor is not in a valid state.");
    }

    @Test
    void getAppliedMentorsOfMentee_withUnavailableData_thenThrowNoContent() {
        doReturn(new ArrayList<>())
                .when(menteeRepository)
                .findAllByProgramIdAndProfileId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.getAppliedMentorsOfMentee(programId, profileId));
        assertThat(thrown)
                .isInstanceOf(NoContentException.class)
                .hasMessage("Error, Mentee by program id: 1 and " +
                            "profile id: 1 doesn't exist.");
    }

    @Test
    void getSelectedMentorOfMentee_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(new ArrayList<>())
                .when(menteeRepository)
                .findAllByProgramIdAndProfileId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.getSelectedMentor(programId, profileId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentee by program id: 1 and " +
                            "profile id: 1 doesn't exist.");
    }

    @Test
    void getSelectedMentorOfMentee_withUnavailableData_thenThrowNoContent() {
        List<Mentee> mentees = new ArrayList<>();
        mentees.add(new Mentee("SubmissionURL"));
        doReturn(mentees)
                .when(menteeRepository)
                .findAllByProgramIdAndProfileId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.getSelectedMentor(programId, profileId));
        assertThat(thrown)
                .isInstanceOf(NoContentException.class)
                .hasMessage("Error, Mentee is not approved by any mentor yet.");
    }
}
