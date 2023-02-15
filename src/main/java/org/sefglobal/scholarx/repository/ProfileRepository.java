package org.sefglobal.scholarx.repository;

import org.sefglobal.scholarx.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUid(String uid);
    Profile findByEmail(String email);
    Boolean existsByUid(String uid);
    Boolean existsByEmail(String email);
    Profile processUserRegistration(Map<String, Object> attributes);
}
