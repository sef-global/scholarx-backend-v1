package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.OAuth2AuthenticationProcessingException;
import org.sefglobal.scholarx.exception.UserAlreadyExistsAuthenticationException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.oauth.GoogleOAuth2UserInfo;
import org.sefglobal.scholarx.repository.ProfileRepository;
import org.sefglobal.scholarx.util.ProfileType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class ProfileService {
    private final static Logger log = LoggerFactory.getLogger(ProfileService.class);
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile processUserRegistration(Map<String, Object> attributes)
            throws OAuth2AuthenticationProcessingException, UserAlreadyExistsAuthenticationException {
        GoogleOAuth2UserInfo oAuth2UserInfo = new GoogleOAuth2UserInfo(attributes);
        if (StringUtils.isEmpty(oAuth2UserInfo.getName())) {
            throw new OAuth2AuthenticationProcessingException("Name not found from OAuth2 provider");
        } else if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        Optional<Profile> profile = profileRepository.findByUid(oAuth2UserInfo.getId());
        if (profile.isPresent()) {
            profile.get().setName(oAuth2UserInfo.getName());
            profile.get().setImageUrl(oAuth2UserInfo.getImageUrl());
            return profileRepository.save(profile.get());
        } else {
            return createProfile(oAuth2UserInfo);
        }
    }

    public Profile createProfile(GoogleOAuth2UserInfo oAuth2UserInfo) throws UserAlreadyExistsAuthenticationException {
        if (oAuth2UserInfo.getId() != null && profileRepository.existsByUid(oAuth2UserInfo.getId())) {
            throw new UserAlreadyExistsAuthenticationException("User with Uid " + oAuth2UserInfo.getId() + " already exist");
        } else if (profileRepository.existsByEmail(oAuth2UserInfo.getEmail())) {
            throw new UserAlreadyExistsAuthenticationException("User with email id " + oAuth2UserInfo.getEmail() + " already exist");
        }
        Profile profile = buildProfile(oAuth2UserInfo);
        return profileRepository.save(profile);
    }

    private Profile buildProfile(GoogleOAuth2UserInfo oAuth2UserInfo) {
        Profile profile = new Profile();
        profile.setName(oAuth2UserInfo.getName());
        profile.setEmail(oAuth2UserInfo.getEmail());
        profile.setUid(oAuth2UserInfo.getId());
        profile.setType(ProfileType.DEFAULT);
        profile.setImageUrl(oAuth2UserInfo.getImageUrl());
        Date now = Calendar.getInstance().getTime();
        profile.setHasConfirmedUserDetails(false);
        profile.setCreatedAt(now);
        profile.setUpdatedAt(now);
        return profile;
    }

    public Profile getLoggedUser(long profileId)
            throws ResourceNotFoundException {
        Optional<Profile> optionalProfile = profileRepository.findById(profileId);
        if (!optionalProfile.isPresent()) {
			String msg = "Error, User with id: " + profileId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return optionalProfile.get();
    }

    public Profile updateUserDetails(long profileId, Profile profile)
            throws ResourceNotFoundException {
        Optional <Profile> optionalUser = profileRepository.findById(profileId);
        if (!optionalUser.isPresent()) {
            String msg = "Error, Unable update details. " +
                    "Profile with id: " + profileId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        optionalUser.get().setEmail(profile.getEmail());
        optionalUser.get().setHasConfirmedUserDetails(true);
        return profileRepository.save(optionalUser.get());
    }
}
