package org.sefglobal.scholarx.controller;

import java.util.Map;
import javax.validation.Valid;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.service.MenteeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mentees")
public class MenteeController {

    private final MenteeService menteeService;

    public MenteeController(MenteeService menteeService) {
        this.menteeService = menteeService;
    }

    @PutMapping("/{id}/state")
    @ResponseStatus(HttpStatus.OK)
    public Mentee approveOrRejectMentee(@PathVariable long id,
                                        @Valid @RequestBody Map<String, Boolean> payload)
            throws ResourceNotFoundException, BadRequestException {
        if (!payload.containsKey("isApproved")) {
            String msg = "Error, Value cannot be null.";
            throw new BadRequestException(msg);
        }
        return menteeService.approveOrRejectMentee(id, payload.get("isApproved"));
    }
}
