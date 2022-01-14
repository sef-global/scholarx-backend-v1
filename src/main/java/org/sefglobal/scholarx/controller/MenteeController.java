package org.sefglobal.scholarx.controller;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.model.Comment;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.service.CommentService;
import org.sefglobal.scholarx.service.MenteeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mentees")
public class MenteeController {

    private final MenteeService menteeService;
    private final CommentService commentService;

    public MenteeController(MenteeService menteeService,CommentService commentService) {
        this.menteeService = menteeService;
        this.commentService = commentService;
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(
            Authentication authentication,
            @Valid @RequestBody Comment comment,
            @PathVariable long id)
            throws ResourceNotFoundException, UnauthorizedException {
        Profile profile = (Profile) authentication.getPrincipal();
        return commentService.addMenteeComment(id,profile.getId(), comment);
    }

    @GetMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<Comment> getMenteesComments(
            Authentication authentication,
            @PathVariable long id
    )
            throws ResourceNotFoundException, BadRequestException, UnauthorizedException {
        Profile profile = (Profile) authentication.getPrincipal();
        return commentService.getAllMenteeComments(id,profile.getId());
    }

    @PutMapping("/comment/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Comment updateComment(@PathVariable long id,
                                        Authentication authentication,
                                        @Valid @RequestBody Comment comment)
            throws ResourceNotFoundException, BadRequestException, UnauthorizedException {
        Profile profile = (Profile) authentication.getPrincipal();
        return commentService.updateComment(id, profile.getId(), comment);
    }

    @DeleteMapping("/comment/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenteeComment(@PathVariable long id,Authentication authentication)
            throws ResourceNotFoundException, UnauthorizedException {
        Profile profile = (Profile) authentication.getPrincipal();
        commentService.deleteComment(id, profile.getId());
    }

    @PutMapping("/{id}/state")
    @ResponseStatus(HttpStatus.OK)
    public Mentee approveOrRejectMentee(@PathVariable long id,
                                        Authentication authentication,
                                        @Valid @RequestBody Map<String, Boolean> payload)
            throws ResourceNotFoundException, BadRequestException, UnauthorizedException {
        Profile profile = (Profile) authentication.getPrincipal();
        if (!payload.containsKey("isApproved")) {
            String msg = "Error, Value cannot be null.";
            throw new BadRequestException(msg);
        }
        return menteeService.approveOrRejectMentee(id, profile.getId(), payload.get("isApproved"));
    }
}
