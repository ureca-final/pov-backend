package net.pointofviews.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pointofviews.common.domain.SoftDeleteEntity;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends SoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;

    private String profileImage;

    private LocalDate birth;

    private String nickname;

    private String socialType;

    private String role;

    private boolean isNoticeActive;

    @Builder
    private Member(LocalDate birth, String email, String nickname, String profileImage, String role, String socialType) {
        this.birth = birth;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.role = role;
        this.socialType = socialType;
        this.isNoticeActive = true;
    }
}
