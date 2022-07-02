package org.sefglobal.scholarx.repository;

import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface MenteeRepository extends JpaRepository<Mentee, Long> {

    List<Mentee> findAllByAssignedMentorIdAndState(long id, EnrolmentState state);

    List<Mentee> findAllByAssignedMentorIdAndStateIn(long id, List<EnrolmentState> states);

    List<Mentee> findAllByProgramIdAndProfileIdAndState(long programId, long profileId, EnrolmentState state);

    Optional<Mentee> findByProfileIdAndAppliedMentorId(long profileId, long mentorId);

    Optional<Mentee> findByProgramIdAndProfileId(long programId, long profileId);

    List<Mentee> findAllByProfileId(long profileId);

    List<Mentee> findAllByProgramId(long id);

    List<Mentee> findAllByProgramIdAndState(long programId, EnrolmentState state);

    List<Mentee> findAllByProgramIdAndStateIn(long programId, List<EnrolmentState> states);

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

    @Modifying
    @Query(
            value = "UPDATE " +
                        "mentee " +
                    "SET state = 'REMOVED' " +
                    "WHERE profile_id = :profileId " +
                      "AND program_id = :programId " +
                      "AND mentor_id != :mentorId",
            nativeQuery = true
    )
    void removeAllByProgramIdAndProfileIdAndMentorIdNot(long programId, long profileId, long mentorId);

    @Modifying
    @Query(
            value = "UPDATE " +
                    "mentee " +
                    "SET state = 'REMOVED' " +
                    "WHERE profile_id = :profileId " +
                    "AND program_id = :programId ",
            nativeQuery = true
    )
    void removeAllByProgramIdAndProfileId(long programId, long profileId);

}
