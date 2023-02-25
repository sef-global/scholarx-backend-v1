package org.sefglobal.scholarx.repository;

import org.sefglobal.scholarx.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUid(String uid);
    Boolean existsByUid(String uid);
    Boolean existsByEmail(String email);
}
