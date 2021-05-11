package org.sefglobal.scholarx.controller.admin;

import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Question;
import org.sefglobal.scholarx.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Question> editQuestions(@RequestBody List<Question> questions) throws ResourceNotFoundException {
        return questionService.editQuestions(questions);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(@PathVariable long id) throws ResourceNotFoundException{
        questionService.deleteQuestion(id);
    }
}
