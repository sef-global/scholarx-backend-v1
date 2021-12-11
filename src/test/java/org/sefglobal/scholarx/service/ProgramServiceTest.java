package org.sefglobal.scholarx.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.*;
import org.sefglobal.scholarx.repository.*;
import org.sefglobal.scholarx.util.ProgramState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
                    "https://scholarx/images/SCHOLARX-2020",
                    "https://scholarx/SCHOLARX-2020/home", ProgramState.CREATED);
    private final Mentor mentor = new Mentor();
    private final Profile profile = new Profile();

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
                () -> programService.getAllMentorsByProgramId(programId, Collections.emptyList()));
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
                        "https://scholarx/images/SCHOLARX-2020",
                        "https://scholarx/SCHOLARX-2020/home",
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
                        "https://scholarx/images/SCHOLARX-2020",
                        "https://scholarx/SCHOLARX-2020/home",
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
    void updateMentorApplication_withValidData_thenReturnUpdatedData()
            throws ResourceNotFoundException, BadRequestException {
        final Program program =
                new Program("SCHOLARX-2020", "SCHOLARX program of 2020",
                        "https://scholarx/images/SCHOLARX-2020",
                        "https://scholarx/SCHOLARX-2020/home",
                        ProgramState.MENTOR_APPLICATION);
        mentor.setProgram(program);

        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findByProfileIdAndProgramId(anyLong(), anyLong());
        doReturn(mentor)
                .when(mentorRepository)
                .save(any(Mentor.class));

        Mentor savedMentor = programService.updateMentorApplication(programId, profileId, mentor);
        assertThat(savedMentor).isNotNull();
    }

    @Test
    void updateMentorApplication_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(mentorRepository)
                .findByProfileIdAndProgramId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.updateMentorApplication(programId, profileId, mentor));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Unable to update mentor application. " +
                            "Mentor with profile id: 1 doesn't exist.");
    }

    @Test
    void updateMentorApplication_withUnsuitableData_thenThrowBadRequest() {
        final Program program =
                new Program("SCHOLARX-2020", "SCHOLARX program of 2020",
                        "https://scholarx/images/SCHOLARX-2020",
                        "https://scholarx/SCHOLARX-2020/home",
                        ProgramState.MENTOR_SELECTION);
        mentor.setProgram(program);

        doReturn(Optional.of(mentor))
                .when(mentorRepository)
                .findByProfileIdAndProgramId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.updateMentorApplication(programId, profileId, mentor));
        assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Error, Unable to update mentor application. " +
                            "Program with id: 1 is not in the valid state.");
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
    void getAppliedMentorOfMentee_withUnavailableData_thenThrowNoContent() {
        doReturn(Optional.empty())
                .when(menteeRepository)
                .findByProgramIdAndProfileId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.getAppliedMentorOfMentee(programId, profileId));
        assertThat(thrown)
                .isInstanceOf(NoContentException.class)
                .hasMessage("Error, Mentee by program id: 1 and " +
                            "profile id: 1 doesn't exist.");
    }

    @Test
    void getSelectedMentorOfMentee_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(menteeRepository)
                .findByProgramIdAndProfileId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.getSelectedMentor(programId, profileId));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Mentee by program id: 1 and " +
                            "profile id: 1 doesn't exist.");
    }

    @Test
    void getSelectedMentorOfMentee_withUnavailableData_thenThrowNoContent() {
        Mentee mentee = new Mentee();
        doReturn(Optional.of(mentee))
                .when(menteeRepository)
                .findByProgramIdAndProfileId(anyLong(), anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.getSelectedMentor(programId, profileId));
        assertThat(thrown)
                .isInstanceOf(NoContentException.class)
                .hasMessage("Error, Mentee is not approved by any mentor yet.");
    }
}
