package org.sefglobal.scholarx.repository;

import org.sefglobal.scholarx.model.Mentee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface MenteeRepository extends JpaRepository<Mentee, Long> {

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
