package org.sefglobal.scholarx.repository;

import org.sefglobal.scholarx.model.MentorResponse;
import org.sefglobal.scholarx.model.MentorResponseId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorResponseRepository extends JpaRepository<MentorResponse, MentorResponseId> {
    List<MentorResponse> getAllByMentorId(long mentorId);
}
