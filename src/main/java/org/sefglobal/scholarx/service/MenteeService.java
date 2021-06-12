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
        menteeRepository.deleteById(id);
    }

    /**
     * Update a {@link EnrolmentState} of a {@link Mentee} to Approved or Rejected
     *
     * @param menteeId   which is the {@link Mentee} to be updated
     * @param isApproved which states whether the {@link Mentee} to be approved or rejected
     * @return the updated {@link Mentee}
     *
     * @throws ResourceNotFoundException is thrown if the {@link Mentee} doesn't exist
     * @throws BadRequestException       is thrown if the {@link Mentee} is removed
     * @throws BadRequestException       is thrown if the {@link Boolean} is null
     */
    public Mentee approveOrRejectMentee(long menteeId, Boolean isApproved)
            throws ResourceNotFoundException, BadRequestException {
        if (null == isApproved){
            String msg = "Error, Value cannot be null.";
            log.error(msg);
            throw new BadRequestException(msg);
        }
        Optional<Mentee> optionalMentee = menteeRepository.findById(menteeId);
        if (!optionalMentee.isPresent()) {
            String msg = "Error, Mentee cannot be approved/rejected. " +
                         "Mentee with id: " + menteeId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        if (EnrolmentState.REMOVED.equals(optionalMentee.get().getState())) {
            String msg = "Error, Mentee cannot be approved/rejected. " +
                         "Mentee with id: " + menteeId + " is removed.";
            log.error(msg);
            throw new BadRequestException(msg);
        }

        optionalMentee.get().setState(isApproved?EnrolmentState.APPROVED:EnrolmentState.REJECTED);
        return menteeRepository.save(optionalMentee.get());
    }
}
