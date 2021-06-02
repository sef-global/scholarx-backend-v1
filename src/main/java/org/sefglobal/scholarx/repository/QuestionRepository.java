package org.sefglobal.scholarx.repository;

import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.model.Question;
import org.sefglobal.scholarx.util.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> getAllByCategoryAndProgramId(QuestionCategory category, long programId);
}
