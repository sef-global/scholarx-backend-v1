package org.sefglobal.scholarx.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.repository.ProgramRepository;
import org.sefglobal.scholarx.util.ProgramState;

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
    @InjectMocks
    private ProgramService programService;
    private final Long programId = 1L;
    private final Program program =
            new Program("SCHOLARX-2020", "SCHOLARX program of 2020",
                        "http://scholarx/images/SCHOLARX-2020",
                        "http://scholarx/SCHOLARX-2020/home", ProgramState.CREATED);

    @Test
    void updateState_withValidData_thenReturnUpdatedData() throws ResourceNotFoundException {
        doReturn(Optional.of(program))
                .when(programRepository)
                .findById(anyLong());
        doReturn(program)
                .when(programRepository)
                .save(any(Program.class));

        Program savedProgram = programService.updateState(programId, program.getState());
        assertThat(savedProgram).isNotNull();
    }

    @Test
    void updateState_withUnavailableData_thenThrowResourceNotFound() {
        doReturn(Optional.empty())
                .when(programRepository)
                .findById(anyLong());

        Throwable thrown = catchThrowable(
                () -> programService.updateState(programId, program.getState()));
        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Error, Program with id: 1 cannot be updated. Program doesn't exist.");
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
                .hasMessage("Error, Program with id: 1 cannot be deleted. Program doesn't exist.");
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
}
