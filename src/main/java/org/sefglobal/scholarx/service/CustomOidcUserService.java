package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.OAuth2AuthenticationProcessingException;
import org.sefglobal.scholarx.model.Profile;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final ProfileService profileService;

    public CustomOidcUserService(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Authorizes the current user and create a {@link Profile} if user haven't registered
     *
     * @param oidcUserRequest which is the {@link OidcUserRequest}
     * @throws OAuth2AuthenticationException if authentication fails
     */
    @Override
    public OidcUser loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(oidcUserRequest);
        try {
            return profileService.processUserRegistration(oidcUser.getAttributes());
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new OAuth2AuthenticationProcessingException(ex.getMessage(), ex.getCause());
        }
    }
}
