package org.sefglobal.scholarx.oauth;

import java.util.List;
import java.util.Map;

public class LinkedInAuthUserInfo {
    private final Map<String, Object> attributes;

    public LinkedInAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getUuid() {
        return attributes.get("id").toString();
    }

    public String getFirstName() {
        return attributes.get("localizedFirstName").toString();
    }

    public String getLastName() {
        return attributes.get("localizedLastName").toString();
    }

    public String getEmail() {
        return attributes.get("emailAddress").toString();
    }

    public String getImageUrl() {
        return attributes.get("imageUrl").toString();
    }
}
