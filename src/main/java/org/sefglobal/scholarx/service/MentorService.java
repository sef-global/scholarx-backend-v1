package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MentorService {

    private final static Logger log = LoggerFactory.getLogger(MentorService.class);
    private final MentorRepository mentorRepository;

    public MentorService(MentorRepository mentorRepository) {
        this.mentorRepository = mentorRepository;
    }

    /**
     * Update a {@link EnrolmentState} of a {@link Mentor}
     *
     * @param id             which is the {@link Mentor} to be updated
     * @param enrolmentState which is the {@link EnrolmentState} of the program to be updated
     * @return the updated {@link Mentor}
     * @throws ResourceNotFoundException is thrown if the requesting {@link Mentor} doesn't exist
     */
    public Mentor updateState(long id, EnrolmentState enrolmentState)
            throws ResourceNotFoundException {
        Optional<Mentor> optionalMentor = mentorRepository.findById(id);
        if (!optionalMentor.isPresent()) {
            String msg = "Error, Mentor with id: " + id + " cannot be updated. " +
                         "Mentor doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        optionalMentor.get().setState(enrolmentState);
        return mentorRepository.save(optionalMentor.get());
    }
}
