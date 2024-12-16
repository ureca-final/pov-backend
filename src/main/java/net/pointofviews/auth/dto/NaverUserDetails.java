package net.pointofviews.auth.dto;

import java.util.Map;

public record NaverUserDetails(
        Map<String, Object> attributes
) implements OAuth2UserInfo {

    public NaverUserDetails(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getNickname() {
        return attributes.get("nickname").toString();
    }

    @Override
    public String getProfileImage() {
        return attributes.get("profile_image").toString();
    }

    @Override
    public String getBirthday() {
        return attributes.get("birthday").toString();
    }

    @Override
    public String getBirthYear() {
        return attributes.get("birthyear").toString();
    }
}
