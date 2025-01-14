package net.pointofviews.club.domain;

import net.pointofviews.common.domain.BaseEntity;
import net.pointofviews.member.domain.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberClub extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Club club;

    private boolean isLeader;

    @Builder
    private MemberClub(Club club, boolean isLeader, Member member) {
        this.club = club;
        this.isLeader = isLeader;
        this.member = member;
    }

    public void updateLeaderStatus(boolean isLeader) {
        this.isLeader = isLeader;
    }
}
