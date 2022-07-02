package org.sefglobal.scholarx.repository;

import org.sefglobal.scholarx.model.SentEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<SentEmail, Long> {
    List<SentEmail> findAllByProgram(long id);
}
