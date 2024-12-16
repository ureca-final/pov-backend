package net.pointofviews.auth.dto;

public interface OAuth2UserInfo {
    String getEmail();

    String getNickname();

    String getProfileImage();

    String getBirthday();

    String getBirthYear();
}
