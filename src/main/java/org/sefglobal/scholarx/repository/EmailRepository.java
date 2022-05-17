package org.sefglobal.scholarx.repository;

import org.sefglobal.scholarx.model.SentEmails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<SentEmails, Long> {
    List<SentEmails> findAllByProgram(long id);
}
