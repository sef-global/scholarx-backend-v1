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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentees")
public class MenteeController {

    private final MenteeService menteeService;
    private final CommentService commentService;

    public MenteeController(MenteeService menteeService,CommentService commentService) {
        this.menteeService = menteeService;
        this.commentService = commentService;
    }

    @PostMapping("/{id}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment addComment(
            Authentication authentication,
            @Valid @RequestBody Map<String, String> payload,
            @PathVariable long id)
            throws ResourceNotFoundException, UnauthorizedException {
        Profile profile = (Profile) authentication.getPrincipal();
        return commentService.addMenteeComment(id,profile.getId(), payload.get("comment"));
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
                                        @Valid @RequestBody Map<String, String> payload)
            throws ResourceNotFoundException, BadRequestException, UnauthorizedException {
        Profile profile = (Profile) authentication.getPrincipal();
        if (!payload.containsKey("comment")) {
            String msg = "Error, Value cannot be null.";
            throw new BadRequestException(msg);
        }
        return commentService.updateComment(id, profile.getId(), payload.get("comment"));
    }

    @DeleteMapping("/comment/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentee(@PathVariable long id)
            throws ResourceNotFoundException {
        commentService.deleteComment(id);
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
