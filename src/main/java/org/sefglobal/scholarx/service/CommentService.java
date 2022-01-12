package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.model.Comment;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.repository.CommentRepository;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final static Logger log = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;
    private final ProfileRepository profileRepository;
    private final MenteeRepository menteeRepository;

    public CommentService(ProfileRepository profileRepository,MenteeRepository menteeRepository,
                                CommentRepository commentRepository){
        this.commentRepository = commentRepository;
        this.profileRepository = profileRepository;
        this.menteeRepository = menteeRepository;
    }

    public List<Comment> getAllMenteeComments(long menteeId, long profileId)
            throws ResourceNotFoundException, BadRequestException, UnauthorizedException {
        Optional<Mentee> optionalMentee = menteeRepository.findById(menteeId);

        if (!optionalMentee.isPresent()) {
            String msg = "Error, Mentee by id: " + menteeId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        } else if (optionalMentee.get().getProfile().getId() == profileId) {
            String msg = "Error, User by id: " + menteeId + " is not allowed access.";
            log.error(msg);
            throw new UnauthorizedException(msg);
        }

        return commentRepository.findAllByMenteeId(menteeId);
    }

    public Comment addMenteeComment(long menteeId, long profileId, String menteeComment)
            throws ResourceNotFoundException, UnauthorizedException {
        Optional<Profile> optionalProfile = profileRepository.findById(profileId);
        Optional<Mentee> optionalMentee = menteeRepository.findById(menteeId);

        if (!optionalMentee.isPresent()) {
            String msg = "Error, Mentee by id: " + menteeId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        if (!optionalProfile.isPresent()) {
            String msg = "Error, User by id: " + profileId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        } else if (optionalMentee.get().getProfile().getId() == profileId) {
            String msg = "Error, User by id: " + menteeId + " is not allowed access.";
            log.error(msg);
            throw new UnauthorizedException(msg);
        }

        Comment comment = new Comment();
        comment.setProfile(optionalProfile.get());
        comment.setComment(menteeComment);
        comment.setMentee(optionalMentee.get());
        return commentRepository.save(comment);
    }

    public Comment updateComment(long id, long profileId, String menteeComment)
            throws ResourceNotFoundException {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (!optionalComment.isPresent()) {
            String msg = "Error, Comment with id: " + id + " cannot be deleted. " +
                    "Comment doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        optionalComment.get().setComment(menteeComment);
        return commentRepository.save(optionalComment.get());
    }

    public void deleteComment(long id)
            throws ResourceNotFoundException {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (!optionalComment.isPresent()) {
            String msg = "Error, Comment with id: " + id + " cannot be deleted. " +
                    "Comment doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        commentRepository.deleteById(id);
    }
}
