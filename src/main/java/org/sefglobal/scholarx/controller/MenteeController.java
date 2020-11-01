package org.sefglobal.scholarx.controller;

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

import javax.validation.Valid;

@RestController
@RequestMapping("/mentees")
public class MenteeController {

    private final MenteeService menteeService;

    public MenteeController(MenteeService menteeService) {
        this.menteeService = menteeService;
    }


    @PutMapping("/{id}/state")
    @ResponseStatus(HttpStatus.OK)
    public Mentee approveOrRejectMentee(@PathVariable long id,
                                        @Valid @RequestBody boolean isApproved)
            throws ResourceNotFoundException, BadRequestException {
        return menteeService.approveOrRejectMentee(id, isApproved);
    }
}
