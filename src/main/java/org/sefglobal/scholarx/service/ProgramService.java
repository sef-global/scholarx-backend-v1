package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.repository.ProgramRepository;
import org.sefglobal.scholarx.util.ProgramState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProgramService {
    private final static Logger log = LoggerFactory.getLogger(ProgramService.class);
    private final ProgramRepository programRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    public ProgramService(ProgramRepository programRepository,
                          MentorRepository mentorRepository,
                          MenteeRepository menteeRepository) {
        this.programRepository = programRepository;
        this.mentorRepository = mentorRepository;
        this.menteeRepository = menteeRepository;
    }

    /**
     * Retrieves all the {@link Program} objects
     *
     * @return {@link List} of {@link Program} objects
     */
    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }

    /**
     * Retrieves the {@link Program} filtered from {@code id}
     *
     * @param id which is the id of the filtering {@link Program}
     * @return {@link Program}
     *
     * @throws ResourceNotFoundException if the requesting {@link Program} doesn't exist
     */
    public Program getProgramById(long id) throws ResourceNotFoundException {
        Optional<Program> program = programRepository.findById(id);
        if (!program.isPresent()) {
            String msg = "Error, Program by id: " + id + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return program.get();
    }

    /**
     * Create new {@link Program}
     *
     * @param program which holds the data to be added
     * @return the created {@link Program}
     */
    public Program addProgram(Program program) {
        return programRepository.save(program);
    }

    /**
     * Update a {@link Program} by editing {@link ProgramState}
     *
     * @param id    which is the {@link Program} to be updated
     * @param state which is the updated {@link ProgramState}
     * @return the updated {@link Program}
     * @throws ResourceNotFoundException is thrown if the requesting {@link Program} doesn't exist
     */
    public Program updateState(long id, ProgramState state)
            throws ResourceNotFoundException {
        Optional<Program> program = programRepository.findById(id);
        if (!program.isPresent()) {
            String msg = "Error, Program with id: " + id + " cannot be updated. " +
                         "Program doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        program.get().setState(state);
        return programRepository.save(program.get());
    }

    /**
     * Delete a existing {@link Program}
     *
     * @param id which is the identifier of the {@link Program}
     * @throws ResourceNotFoundException if {@link Program} for {@code id} doesn't exist
     */
    public void deleteProgram(long id) throws ResourceNotFoundException {
        Optional<Program> existingProgram = programRepository.findById(id);
        if (!existingProgram.isPresent()) {
            String msg = "Error, Program with id: " + id + " cannot be deleted. " +
                         "Program doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        menteeRepository.deleteByMentorProgramId(id);
        mentorRepository.deleteByProgramId(id);
        programRepository.deleteById(id);
    }

    /**
     * Retrieves all the {@link Mentor} objects filtered from {@link Program} {@code id}
     *
     * @param id which is the Program id of the filtering {@link Mentor} objects
     * @return {@link List} of {@link Mentor} objects
     * @throws ResourceNotFoundException if the requesting {@link Program} to filter {@link
     *                                   Mentor} objects doesn't exist
     */
    public List<Mentor> getAllMentorsByProgramId(long id)
            throws ResourceNotFoundException {
        if (!programRepository.existsById(id)) {
            String msg = "Error, Program by id: " + id + " doesn't exist";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return mentorRepository.findAllByProgramId(id);
    }
}
