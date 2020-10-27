package org.sefglobal.scholarx.repository;

import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface MenteeRepository extends JpaRepository<Mentee, Long> {

    List<Mentee> findAllByMentorIdAndState(long id, EnrolmentState state);
    List<Mentee> findAllByMentorIdAndStateIn(long id, List<EnrolmentState> states);

    @Modifying
    @Query(
            value = "DELETE " +
                    "FROM mentee " +
                    "WHERE mentor_id IN ( " +
                    "    SELECT mentor_id " +
                    "    FROM mentor " +
                    "    WHERE mentor.program_id = :id)",
            nativeQuery = true
    )
    void deleteByMentorProgramId(long id);
}
