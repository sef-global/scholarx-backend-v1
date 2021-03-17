package org.sefglobal.scholarx.repository;

import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.util.ProgramState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    List<Program> findAllByStateIn(List<ProgramState> states);
}
