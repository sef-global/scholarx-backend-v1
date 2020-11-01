package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.util.EnrolmentState;
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

    /**
     * Update a {@link EnrolmentState} of a {@link Mentee} to Approved or Rejected
     *
     * @param menteeId   which is the {@link Mentee} to be updated
     * @param isApproved which states whether the {@link Mentee} to be approved or not
     * @return the updated {@link Mentee}
     *
     * @throws ResourceNotFoundException is thrown if the {@link Mentee} doesn't exist
     * @throws BadRequestException       is thrown if the {@link Mentee} is not in valid state
     */
    public Mentee approveOrRejectMentee(long menteeId, boolean isApproved)
            throws ResourceNotFoundException, BadRequestException {
        Optional<Mentee> optionalMentee = menteeRepository.findById(menteeId);
        if (!optionalMentee.isPresent()) {
            String msg = "Error, Mentee cannot be approved/rejected. " +
                         "Mentee with id: " + menteeId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        if (!(EnrolmentState.PENDING.equals(optionalMentee.get().getState()))) {
            String msg = "Error, Mentee cannot be approved/rejected. " +
                         "Mentee with id: " + menteeId + " is not in the valid state.";
            log.error(msg);
            throw new BadRequestException(msg);
        }

        if (isApproved) {
            optionalMentee.get().setState(EnrolmentState.APPROVED);
        } else {
            optionalMentee.get().setState(EnrolmentState.REJECTED);
        }
        return menteeRepository.save(optionalMentee.get());
    }
}
