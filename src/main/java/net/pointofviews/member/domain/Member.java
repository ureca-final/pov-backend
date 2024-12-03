package net.pointofviews.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.SoftDeleteEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends SoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String email;

    private String profileImage;

    private LocalDate birth;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    private boolean isNoticeActive;

    @Builder
    private Member(LocalDate birth, String email, String nickname, String profileImage, RoleType roleType, SocialType socialType) {
        this.birth = birth;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.roleType = roleType;
        this.socialType = socialType;
        this.isNoticeActive = true;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void delete() {
        this.setDeletedAt(LocalDateTime.now());
    }
}
