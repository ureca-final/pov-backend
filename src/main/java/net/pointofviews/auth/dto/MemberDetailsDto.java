package net.pointofviews.auth.dto;

import net.pointofviews.member.domain.Member;
import net.pointofviews.member.domain.RoleType;
import net.pointofviews.member.domain.SocialType;

import java.time.LocalDate;
import java.util.UUID;

public record MemberDetailsDto(
        UUID id,
        String email,
        String profileImage,
        LocalDate birth,
        String nickname,
        SocialType socialType,
        RoleType roleType,
        boolean isNoticeActive,
        Member member
) {
    public static MemberDetailsDto from(Member member) {
        return new MemberDetailsDto(
                member.getId(),
                member.getEmail(),
                member.getProfileImage(),
                member.getBirth(),
                member.getNickname(),
                member.getSocialType(),
                member.getRoleType(),
                member.isNoticeActive(),
                member
        );
    }
}