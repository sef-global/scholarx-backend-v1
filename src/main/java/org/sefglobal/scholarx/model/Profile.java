package org.sefglobal.scholarx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;
import org.sefglobal.scholarx.util.ProfileType;
import org.sefglobal.scholarx.util.Views;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Entity
@Table(name = "profile")
@JsonIgnoreProperties(value = {"createdAt", "updatedAt", "enrolledUsers",
        "authenticationContextClass", "nonce", "subject", "issuer",
        "audience", "expiresAt", "issuedAt", "authenticatedAt",
        "authenticationMethods", "authorizedParty", "accessTokenHash",
        "authorizationCodeHash", "fullName", "subject", "givenName",
        "familyName", "middleName", "nickName", "preferredUsername",
        "profile", "picture", "website", "emailVerified", "gender", "birthdate",
        "zoneInfo", "locale", "phoneNumber", "phoneNumberVerified", "address"})
public class Profile extends BaseScholarxModel implements OidcUser {

  @Column(length = 36, nullable = false)
  private String uid;

  @Column(nullable = false)
  private String email;

  @JsonView(Views.Public.class)
  @Column
  private String firstName;

  @JsonView(Views.Public.class)
  @Column
  private String lastName;

  @JsonView(Views.Public.class)
  @Column
  private String imageUrl;

  @JsonView(Views.Public.class)
  @Column
  private String linkedinUrl;

  @Column
  private Boolean hasConfirmedUserDetails;

  @JsonView(Views.Public.class)
  @Column(length = 50)
  private String headline;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, nullable = false)
  private ProfileType type;

  @OneToMany(mappedBy = "profile")
  private List<EnrolledUser> enrolledUsers = new ArrayList<>();

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void setLinkedinUrl(String linkedinUrl) {
    this.linkedinUrl = linkedinUrl;
  }

  public String getLinkedinUrl() {
    return linkedinUrl;
  }

  public String getHeadline() {
    return headline;
  }

  public void setHeadline(String headline) {
    this.headline = headline;
  }

  public ProfileType getType() {
    return type;
  }

  public void setType(ProfileType type) {
    this.type = type;
  }

  public List<EnrolledUser> getEnrolledUsers() {
    return enrolledUsers;
  }

  public void setEnrolledUsers(List<EnrolledUser> enrolledUsers) {
    this.enrolledUsers = enrolledUsers;
  }

  public Boolean getHasConfirmedUserDetails() {
    return hasConfirmedUserDetails;
  }

  public void setHasConfirmedUserDetails(Boolean hasConfirmedUserDetails) {
    this.hasConfirmedUserDetails = hasConfirmedUserDetails;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return null;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(this.type.toString()));
    return authorities;
  }

  @Override
  public String getName() {
    return getFirstName()+" "+getLastName();
  }

  @Override
  public OidcUserInfo getUserInfo() {
    return null;
  }

  @Override
  public OidcIdToken getIdToken() {
    return null;
  }

  @Override
  public Map<String, Object> getClaims() {
    return null;
  }
}
