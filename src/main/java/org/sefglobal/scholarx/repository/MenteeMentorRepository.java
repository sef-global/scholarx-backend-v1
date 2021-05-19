package org.sefglobal.scholarx.repository;

import org.sefglobal.scholarx.model.MenteeMentor;
import org.sefglobal.scholarx.model.MenteeMentorId;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenteeMentorRepository extends JpaRepository<MenteeMentor, MenteeMentorId> {
    List<MenteeMentor> findAllByMentorIdAndState(long mentorId, EnrolmentState state);
}
