package org.sefglobal.scholarx.controller.admin;

import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.service.ProgramService;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController("ProgramAdminController")
@RequestMapping("/admin/programs")
public class ProgramController {
    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Program addProgram(@Valid @RequestBody Program program) {
        return programService.addProgram(program);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Program updateProgram(@PathVariable long id, @Valid @RequestBody Program program)
            throws ResourceNotFoundException {
        return programService.updateProgram(id, program);
    }

    @PutMapping("/{id}/state")
    @ResponseStatus(HttpStatus.OK)
    public Program updateState(@PathVariable long id) throws ResourceNotFoundException {
        return programService.updateState(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProgram(@PathVariable long id) throws ResourceNotFoundException {
        programService.deleteProgram(id);
    }

    @GetMapping("/{id}/mentors")
    @ResponseStatus(HttpStatus.OK)
    public List<Mentor> getAllMentorsByProgramId(@PathVariable long id)
            throws ResourceNotFoundException {
        return programService.getAllMentorsByProgramId(id);
    }

    @GetMapping("/{id}/mentor/mentees")
    @ResponseStatus(HttpStatus.OK)
    public List<Mentee> getMenteesOfMentor(@PathVariable long id,
                                           @RequestParam(required = false) List<EnrolmentState> states)
            throws ResourceNotFoundException {
        long profileId = 1; // TODO: Get the profileId from headers
        return programService.getAllMenteesOfMentor(id, profileId, states);
    }
}
