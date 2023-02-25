package org.sefglobal.scholarx.oauth;

import java.util.Map;

public class GoogleAuthUserInfo {
    private final Map<String, Object> attributes;

    public GoogleAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getId() {
        return attributes.get("sub").toString();
    }

    public String getName() {
        return attributes.get("name").toString();
    }

    public String getEmail() {
        return attributes.get("email").toString();
    }

    public String getImageUrl() {
        return attributes.get("picture").toString();
    }
}
