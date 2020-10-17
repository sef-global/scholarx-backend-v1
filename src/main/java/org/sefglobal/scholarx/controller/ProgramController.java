package org.sefglobal.scholarx.controller;

import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.service.ProgramService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/programs")
public class ProgramController {
    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Program> getAllPrograms() {
        return programService.getAllPrograms();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Program getProgramById(@PathVariable long id) throws ResourceNotFoundException {
        return programService.getProgramById(id);
    }

    @GetMapping("/{id}/mentors")
    @ResponseStatus(HttpStatus.OK)
    public List<Mentor> getAllMentorsByProgramId(@PathVariable long id)
            throws ResourceNotFoundException {
        return programService.getAllMentorsByProgramId(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Program updateProgram(@PathVariable long id, @RequestBody Program program)
            throws ResourceNotFoundException {
        return programService.updateProgram(program, id);
    }
}
