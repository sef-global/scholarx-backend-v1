package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MenteeService {

    private final static Logger log = LoggerFactory.getLogger(MenteeService.class);
    private final MenteeRepository menteeRepository;

    public MenteeService(MenteeRepository menteeRepository) {
        this.menteeRepository = menteeRepository;
    }

    /**
     * Delete a existing {@link Mentee}
     *
     * @param id which is the identifier of the {@link Mentee}
     * @throws ResourceNotFoundException if {@link Mentee} for {@code id} doesn't exist
     */
    public void deleteMentee(long id)
            throws ResourceNotFoundException {
        Optional<Mentee> optionalMentee = menteeRepository.findById(id);
        if (!optionalMentee.isPresent()) {
            String msg = "Error, Mentee with id: " + id + " cannot be deleted. " +
                         "Mentee doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
    }
}
