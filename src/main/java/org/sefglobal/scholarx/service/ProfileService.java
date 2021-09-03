package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.OAuth2AuthenticationProcessingException;
import org.sefglobal.scholarx.exception.UserAlreadyExistsAuthenticationException;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.oauth.LinkedInAuthUserInfo;
import org.sefglobal.scholarx.repository.ProfileRepository;
import org.sefglobal.scholarx.util.ProfileType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile processUserRegistration(Map<String, Object> attributes)
            throws OAuth2AuthenticationProcessingException, UserAlreadyExistsAuthenticationException {
        LinkedInAuthUserInfo oAuth2UserInfo = new LinkedInAuthUserInfo(attributes);
        if (StringUtils.isEmpty(oAuth2UserInfo.getFirstName())) {
            throw new OAuth2AuthenticationProcessingException("Name not found from OAuth2 provider");
        } else if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        Optional<Profile> profile = profileRepository.findByUid(oAuth2UserInfo.getUuid());
        if (profile.isPresent()) {
            profile.get().setFirstName(oAuth2UserInfo.getFirstName());
            profile.get().setLastName(oAuth2UserInfo.getLastName());
            profile.get().setImageUrl(oAuth2UserInfo.getImageUrl());
            profile.get().setEmail(oAuth2UserInfo.getEmail());
            return profileRepository.save(profile.get());
        } else {
            return createProfile(oAuth2UserInfo);
        }
    }

    public Profile createProfile(LinkedInAuthUserInfo oAuth2UserInfo) throws UserAlreadyExistsAuthenticationException {
        if (oAuth2UserInfo.getUuid() != null && profileRepository.existsByUid(oAuth2UserInfo.getUuid())) {
            throw new UserAlreadyExistsAuthenticationException("User with Uid " + oAuth2UserInfo.getUuid() + " already exist");
        } else if (profileRepository.existsByEmail(oAuth2UserInfo.getEmail())) {
            throw new UserAlreadyExistsAuthenticationException("User with email id " + oAuth2UserInfo.getEmail() + " already exist");
        }
        Profile profile = buildProfile(oAuth2UserInfo);
        return profileRepository.save(profile);
    }

    private Profile buildProfile(LinkedInAuthUserInfo oAuth2UserInfo) {
        Profile profile = new Profile();
        profile.setFirstName(oAuth2UserInfo.getFirstName());
        profile.setLastName(oAuth2UserInfo.getLastName());
        profile.setEmail(oAuth2UserInfo.getEmail());
        profile.setUid(oAuth2UserInfo.getUuid());
        profile.setType(ProfileType.DEFAULT);
        profile.setImageUrl(oAuth2UserInfo.getImageUrl());
//        TODO: set linkedin permissions to get r_basicprofile from the linkedin app
        profile.setLinkedinUrl("https://www.linkedin.com/search/results/all/?keywords="+
                oAuth2UserInfo.getFirstName()+"%20"+
                oAuth2UserInfo.getLastName()+"&origin=TYPEAHEAD_ESCAPE_HATCH");
        Date now = Calendar.getInstance().getTime();
        profile.setCreatedAt(now);
        profile.setUpdatedAt(now);
        return profile;
    }

}
