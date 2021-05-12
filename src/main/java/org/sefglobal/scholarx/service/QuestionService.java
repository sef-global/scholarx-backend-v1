package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Question;
import org.sefglobal.scholarx.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private final Logger log = LoggerFactory.getLogger(QuestionService.class);
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    /**
     * Updates existing {@link Question} records by a given set of {@link Question}
     *
     * @param questions the set of {@link Question} that needs to be updated
     * @return List of updated {@link Question}
     * @throws ResourceNotFoundException if a given {@link Question} doesn't exist on the database
     */
    public List<Question> editQuestions(List<Question> questions) throws ResourceNotFoundException {
        List<Question> updatedQuestions = new ArrayList<>();
        for(Question q: questions) {
            Optional<Question> question = questionRepository.findById(q.getId());
            if (question.isPresent()) {
                question.get().setQuestion(q.getQuestion());
                updatedQuestions.add(question.get());
            } else {
                String msg = "Error, question by id:" + q.getId() + " doesn't exist.";
                log.error(msg);
                throw new ResourceNotFoundException(msg);
            }
        }
        return questionRepository.saveAll(updatedQuestions);
    }

    /**
     * Deletes existing {@link Question} records by a given {@link Question} id
     *
     * @param id which is the id of the {@link Question} that needs to be deleted
     * @throws ResourceNotFoundException if a {@link Question} by the given id doesn't exist
     */
    public void deleteQuestion(long id) throws ResourceNotFoundException {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isPresent()) {
            questionRepository.delete(question.get());
        } else {
            String msg = "Error, question by id:" + id + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
    }
}
