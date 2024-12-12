package net.pointofviews.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import lombok.Builder;
import net.pointofviews.common.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberFcmToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String fcmToken;

    private boolean isActive;

    @Builder
    private MemberFcmToken(Member member, String fcmToken) {
        this.member = member;
        this.fcmToken = fcmToken;
        this.isActive = true;
    }

    public void updateToken(String newToken) {
        this.fcmToken = newToken;
    }
}
