package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.OAuth2AuthenticationProcessingException;
import org.sefglobal.scholarx.model.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthUserService extends DefaultOAuth2UserService {

    private final ProfileService profileService;
    private final Environment env;

    public AuthUserService(ProfileService profileService, Environment env) {
        this.profileService = profileService;
        this.env = env;
    }

    /**
     * Authorizes the current user and create a {@link Profile} if user haven't registered
     *
     * @param oAuth2UserRequest which is the {@link OAuth2UserRequest}
     * @throws OAuth2AuthenticationException if authentication fails
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
            populateEmailAddressFromLinkedIn(oAuth2UserRequest, attributes);
            populateImageUrl(attributes);
            return profileService.processUserRegistration(attributes);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new OAuth2AuthenticationProcessingException(ex.getMessage(), ex.getCause());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void populateEmailAddressFromLinkedIn(OAuth2UserRequest oAuth2UserRequest, Map<String, Object> attributes) throws OAuth2AuthenticationException {
        String emailEndpointUri = env.getProperty("spring.security.oauth2.client.provider.linkedin.email-address-uri");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + oAuth2UserRequest.getAccessToken().getTokenValue());
        HttpEntity<?> entity = new HttpEntity<>("", headers);
        ResponseEntity<Map> response = restTemplate.exchange(emailEndpointUri, HttpMethod.GET, entity, Map.class);
        List<?> list = (List<?>) response.getBody().get("elements");
        Map map = (Map<?, ?>) ((Map<?, ?>) list.get(0)).get("handle~");
        attributes.putAll(map);
    }

    @SuppressWarnings("rawtypes")
    public void populateImageUrl(Map<String, Object> attributes) {
        if (attributes.get("profilePicture") != null) {
            Map profilePictureObject = (Map<?, ?>) attributes.get("profilePicture");
            Map imageMetaData = (Map<?, ?>) profilePictureObject.get("displayImage~");
            List<?> elements = (List<?>) imageMetaData.get("elements");
            List<?> identifiers = (List<?>) ((Map<?, ?>) elements.get(0)).get("identifiers");
            Map image = (Map<?, ?>) identifiers.get(0);
            attributes.put("imageUrl", image.get("identifier"));
        } else {
            // Default profile image (If user has no LinkedIn profile image)
            attributes.put("imageUrl", "https://res.cloudinary.com/dsxobn1ln/image/upload/v1626966152/profile-pic_hvfryw.jpg");
        }
    }
}
